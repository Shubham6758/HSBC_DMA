package com.coforge.hsbcdma.chatbot.ChatbotUtility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LlmClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getStructuredResponse(String userMessage) {
        try {
            String original = userMessage == null ? "" : userMessage.trim();
            String msg = normalize(original);

            Map<String, Object> action = new HashMap<>();
            Map<String, Object> filters = new HashMap<>();

            String entity = detectEntity(msg);
            String operation = detectOperation(msg);
            String page = detectPage(msg, entity);

            // If user is only asking to open/go to a page
            if (isNavigationOnly(msg)) {
                action.put("action", "NAVIGATE");
                action.put("page", page);
                action.put("entity", entity);
                action.put("operation", "navigate");
                action.put("filters", filters);
                return objectMapper.writeValueAsString(action);
            }

            // Extract filters based on entity
            if ("demands".equals(entity)) {
                extractDemandFilters(original, msg, filters);
            } else if ("profiles".equals(entity)) {
                extractProfileFilters(original, msg, filters);
            } else if ("resources".equals(entity)) {
                extractResourceFilters(original, msg, filters);
            } else if ("tracker".equals(entity)) {
                extractTrackerFilters(original, msg, filters);
            }

            // If no entity found but page found, navigate
            if (entity == null || entity.isBlank()) {
                action.put("action", "TEXT");
                action.put("page", "");
                action.put("entity", "");
                action.put("operation", "");
                action.put("filters", filters);
                return objectMapper.writeValueAsString(action);
            }

            // Count queries should return DATA from backend
            if ("count".equals(operation)) {
                action.put("action", "QUERY");
            } else {
                action.put("action", "NAVIGATE");
            }

            action.put("page", page);
            action.put("entity", entity);
            action.put("operation", operation);
            action.put("filters", filters);

            return objectMapper.writeValueAsString(action);

        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private String normalize(String text) {
        return text == null
                ? ""
                : text.toLowerCase()
                .replace("?", " ")
                .replace(".", " ")
                .replace(",", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isNavigationOnly(String msg) {
        boolean hasNavigationWord =
                msg.contains("go to")
                        || msg.contains("open")
                        || msg.contains("navigate")
                        || msg.contains("take me to")
                        || msg.contains("move to")
                        || msg.contains("switch to");

        boolean hasFilterWord =
                msg.contains("with")
                        || msg.contains("for")
                        || msg.contains("under")
                        || msg.contains("where")
                        || msg.contains("experience")
                        || msg.contains("skill")
                        || msg.contains("status")
                        || msg.contains("count")
                        || msg.contains("how many");

        return hasNavigationWord && !hasFilterWord;
    }

    private String detectEntity(String msg) {
        if (containsAny(msg, "demand", "demands", "requirement", "requirements", "opening", "openings", "rr")) {
            return "demands";
        }

        if (containsAny(msg, "profile", "profiles", "candidate", "candidates", "applicant", "cv", "resume")) {
            return "profiles";
        }

        if (containsAny(msg, "resource", "resources", "employee", "employees", "headcount", "associate")) {
            return "resources";
        }

        if (containsAny(msg, "tracker", "tracking", "interview", "evaluation", "onboarding")) {
            return "tracker";
        }

        if (containsAny(msg, "report", "reports")) {
            return "reports";
        }

        return "";
    }

    private String detectPage(String msg, String entity) {
        if (containsAny(msg, "demand", "demands", "requirement", "requirements", "rr")) {
            return "demands";
        }

        if (containsAny(msg, "profile", "profiles", "candidate", "candidates", "cv", "resume")) {
            return "profiles";
        }

        if (containsAny(msg, "report", "reports")) {
            return "reports";
        }

        if (containsAny(msg, "track", "tracker", "tracking", "interview", "onboarding")) {
            return "track";
        }

        if (containsAny(msg, "resource", "resources", "employee", "employees", "headcount")) {
            return "resources";
        }

        return entity == null ? "" : entity;
    }

    private String detectOperation(String msg) {
        if (containsAny(msg, "how many", "count", "number of", "total", "total number")) {
            return "count";
        }

        if (containsAny(msg, "group by", "grouped by", "wise", "breakdown")) {
            return "group";
        }

        if (containsAny(msg, "top", "highest", "maximum", "most")) {
            return "top";
        }

        if (containsAny(msg, "lowest", "minimum", "least")) {
            return "bottom";
        }

        return "list";
    }

    private boolean containsAny(String msg, String... terms) {
        for (String term : terms) {
            if (msg.contains(term)) return true;
        }
        return false;
    }

    // ================= DEMANDS =================

    private void extractDemandFilters(String original, String msg, Map<String, Object> filters) {
        extractCommonSkillFilter(original, msg, filters);

        String status = extractDemandStatus(msg);
        if (status != null) {
            filters.put("status", status);
        }

        String externality = extractExternality(msg);
        if (externality != null) {
            filters.put("externality", externality);
        }

        String lob = extractAfterKeyword(original, msg, "lob");
        if (lob != null) {
            filters.put("lob", cleanValue(lob));
        }

        String hiringManager = extractAfterKeyword(original, msg, "hiring manager");
        if (hiringManager != null) {
            filters.put("hiringManager", cleanValue(hiringManager));
        }

        String rr = extractRegex(original, "(?i)\\brr\\s*([a-zA-Z0-9_-]+)");
        if (rr != null) {
            filters.put("rr", rr);
        }

        String demandBusinessId = extractRegex(original, "(?i)\\bdemand\\s*(business\\s*)?id\\s*([a-zA-Z0-9_-]+)");
        if (demandBusinessId != null) {
            filters.put("demandBusinessId", demandBusinessId);
        }

        extractDateFilters(msg, filters, "dateProfileShared");
    }

    private String extractDemandStatus(String msg) {
        if (msg.contains("open")) return "OPEN";
        if (msg.contains("closed")) return "CLOSED";
        if (msg.contains("accepted")) return "ACCEPTED";
        if (msg.contains("rejected")) return "REJECTED";
        if (msg.contains("selected")) return "SELECTED";
        return null;
    }

    private String extractExternality(String msg) {
        if (msg.contains("external")) return "EXTERNAL";
        if (msg.contains("internal")) return "INTERNAL";
        return null;
    }

    // ================= PROFILES =================

    private void extractProfileFilters(String original, String msg, Map<String, Object> filters) {
        extractCommonSkillFilter(original, msg, filters);
        extractExperienceFilters(msg, filters);

        String candidateName = extractCandidateName(original, msg);
        if (candidateName != null) {
            filters.put("candidateName", candidateName);
        }

        String recruiter = extractAfterKeyword(original, msg, "recruiter");
        if (recruiter != null) {
            filters.put("recruiter", cleanValue(recruiter));
        }

        String location = extractLocation(original, msg);
        if (location != null) {
            filters.put("location", location);
        }

        String band = extractAfterKeyword(original, msg, "band");
        if (band != null) {
            filters.put("band", cleanValue(band));
        }

        if (msg.contains("active")) {
            filters.put("isActive", true);
        }

        if (msg.contains("inactive")) {
            filters.put("isActive", false);
        }

        String empId = extractRegex(original, "(?i)\\b(emp|employee)\\s*(id)?\\s*([a-zA-Z0-9_-]+)");
        if (empId != null) {
            filters.put("empId", empId);
        }

        String sapId = extractRegex(original, "(?i)\\bsap\\s*(id)?\\s*([a-zA-Z0-9_-]+)");
        if (sapId != null) {
            filters.put("sapId", sapId);
        }

        extractDateFilters(msg, filters, "dateOfSubmission");
    }

    private String extractCandidateName(String original, String msg) {

        String name = null;

        // Most specific patterns first
        name = extractAfterKeyword(original, msg, "candidate name");

        if (name == null) {
            name = extractAfterKeyword(original, msg, "candidate named");
        }

        if (name == null) {
            name = extractAfterKeyword(original, msg, "profile of candidate name");
        }

        if (name == null) {
            name = extractAfterKeyword(original, msg, "profile of candidate");
        }

        if (name == null) {
            name = extractAfterKeyword(original, msg, "profile of");
        }

        if (name == null) {
            name = extractAfterKeyword(original, msg, "candidate");
        }

        if (name == null) {
            name = extractAfterKeyword(original, msg, "named");
        }

        if (name == null) {
            name = extractAfterKeyword(original, msg, "name");
        }

        if (name != null) {
            return cleanCandidateName(name);
        }

        return null;
    }
    private String cleanCandidateName(String value) {
        if (value == null) {
            return null;
        }

        return value
                .replace("candidate name", "")
                .replace("candidate named", "")
                .replace("candidate", "")
                .replace("profile", "")
                .replace("named", "")
                .replace("name", "")
                .replace("please", "")
                .replace("page", "")
                .replace("?", "")
                .replace(".", "")
                .replace(",", "")
                .trim();
    }

    // ================= RESOURCES =================

    private void extractResourceFilters(String original, String msg, Map<String, Object> filters) {
        extractCommonSkillFilter(original, msg, filters);
        extractExperienceFilters(msg, filters);

        String resourceName = extractAfterKeyword(original, msg, "resource named");
        if (resourceName == null) resourceName = extractAfterKeyword(original, msg, "resource name");
        if (resourceName != null) {
            filters.put("resourceName", cleanValue(resourceName));
        }

        String hbu = extractAfterKeyword(original, msg, "hbu");
        if (hbu != null) {
            filters.put("hbu", cleanValue(hbu));
        }

        String lob = extractAfterKeyword(original, msg, "lob");
        if (lob != null) {
            filters.put("lob", cleanValue(lob));
        }

        String location = extractLocation(original, msg);
        if (location != null) {
            filters.put("coforgeLocation", location);
        }

        if (msg.contains("active")) {
            filters.put("isActive", true);
        }

        if (msg.contains("inactive")) {
            filters.put("isActive", false);
        }

        if (msg.contains("replacement required")) {
            filters.put("replacementRequired", "Yes");
        }

        if (msg.contains("replacement given")) {
            filters.put("replacementGiven", "Yes");
        }

        if (msg.contains("onshore")) {
            filters.put("onshoreOffshore", "Onshore");
        }

        if (msg.contains("offshore")) {
            filters.put("onshoreOffshore", "Offshore");
        }

        String manager = extractAfterKeyword(original, msg, "manager");
        if (manager != null) {
            filters.put("functionalMgrHsbc", cleanValue(manager));
        }

        String psid = extractRegex(original, "(?i)\\bpsid\\s*([a-zA-Z0-9_-]+)");
        if (psid != null) {
            filters.put("psid", psid);
        }

        String empId = extractRegex(original, "(?i)\\b(emp|employee)\\s*(id)?\\s*([a-zA-Z0-9_-]+)");
        if (empId != null) {
            filters.put("empId", empId);
        }

        extractRateFilters(msg, filters);
        extractDateFilters(msg, filters, "resourceDate");
    }

    // ================= TRACKER =================

    private void extractTrackerFilters(String original, String msg, Map<String, Object> filters) {
        String candidateName = extractCandidateName(original, msg);
        if (candidateName != null) {
            filters.put("candidateName", candidateName);
        }

        String demandId = extractRegex(original, "(?i)\\bdemand\\s*(id)?\\s*([0-9]+)");
        if (demandId != null) {
            filters.put("demandId", demandId);
        }

        if (msg.contains("interview")) {
            filters.put("trackerDateType", "interviewDate");
        }

        if (msg.contains("decision")) {
            filters.put("trackerDateType", "decisionDate");
        }

        if (msg.contains("attached")) {
            filters.put("trackerDateType", "attachedDate");
        }

        if (msg.contains("shared")) {
            filters.put("trackerDateType", "profileSharedDate");
        }

        extractDateFilters(msg, filters, "trackerDate");
    }

    // ================= COMMON FILTERS =================

    private void extractCommonSkillFilter(String original, String msg, Map<String, Object> filters) {
        String skill = null;

        if (msg.contains("primary skill")) {
            skill = extractAfterKeyword(original, msg, "primary skill");
        }

        if (skill == null && msg.contains("secondary skill")) {
            skill = extractAfterKeyword(original, msg, "secondary skill");
            if (skill != null) {
                filters.put("secondarySkills", cleanValue(skill));
                return;
            }
        }

        if (skill == null && msg.contains("skill")) {
            skill = extractAfterKeyword(original, msg, "skill");
        }

        if (skill == null && msg.contains("for")) {
            skill = extractAfterKeyword(original, msg, "for");
        }

        if (skill == null && msg.contains("with")) {
            skill = extractAfterKeyword(original, msg, "with");
        }

        if (skill != null) {
            skill = removeKnownTrailingWords(skill);
            filters.put("primarySkills", cleanValue(skill));
        }
    }

    private void extractExperienceFilters(String msg, Map<String, Object> filters) {

        // more than 5 years / above 5 years / greater than 5 years / minimum 5 years / at least 5 years
        Matcher minBeforeNumber = Pattern.compile(
                "(more than|above|greater than|at least|minimum|min)\\s*(\\d+(\\.\\d+)?)\\s*(years|year|yrs|yr)?"
        ).matcher(msg);

        if (minBeforeNumber.find()) {
            filters.put("experienceMin", Double.parseDouble(minBeforeNumber.group(2)));
            return;
        }

        // 5+ years / 5 plus years
        Matcher minAfterNumber = Pattern.compile(
                "(\\d+(\\.\\d+)?)\\s*(\\+|plus)\\s*(years|year|yrs|yr)?"
        ).matcher(msg);

        if (minAfterNumber.find()) {
            filters.put("experienceMin", Double.parseDouble(minAfterNumber.group(1)));
            return;
        }

        // less than 5 years / below 5 years / under 5 years / maximum 5 years
        Matcher maxBeforeNumber = Pattern.compile(
                "(less than|below|under|maximum|max|at most)\\s*(\\d+(\\.\\d+)?)\\s*(years|year|yrs|yr)?"
        ).matcher(msg);

        if (maxBeforeNumber.find()) {
            filters.put("experienceMax", Double.parseDouble(maxBeforeNumber.group(2)));
            return;
        }

        // between 2 and 5 years / between 2 to 5 years
        Matcher between = Pattern.compile(
                "between\\s*(\\d+(\\.\\d+)?)\\s*(and|to)\\s*(\\d+(\\.\\d+)?)"
        ).matcher(msg);

        if (between.find()) {
            filters.put("experienceMin", Double.parseDouble(between.group(1)));
            filters.put("experienceMax", Double.parseDouble(between.group(4)));
            return;
        }

        // exactly 5 years experience
        Matcher exact = Pattern.compile(
                "(\\d+(\\.\\d+)?)\\s*(years|year|yrs|yr)\\s*(experience|exp)?"
        ).matcher(msg);

        if (exact.find()) {
            filters.put("experience", Double.parseDouble(exact.group(1)));
        }
    }

    private void extractRateFilters(String msg, Map<String, Object> filters) {
        Matcher above = Pattern.compile("(rate|effective rate|fixed capacity rate)\\s*(above|greater than|more than)\\s*(\\d+(\\.\\d+)?)").matcher(msg);
        if (above.find()) {
            filters.put("rateMin", Double.parseDouble(above.group(3)));
            return;
        }

        Matcher below = Pattern.compile("(rate|effective rate|fixed capacity rate)\\s*(below|less than|under)\\s*(\\d+(\\.\\d+)?)").matcher(msg);
        if (below.find()) {
            filters.put("rateMax", Double.parseDouble(below.group(3)));
        }
    }

    private void extractDateFilters(String msg, Map<String, Object> filters, String fieldPrefix) {
        if (msg.contains("today")) {
            filters.put(fieldPrefix + "Range", "TODAY");
        } else if (msg.contains("yesterday")) {
            filters.put(fieldPrefix + "Range", "YESTERDAY");
        } else if (msg.contains("this week")) {
            filters.put(fieldPrefix + "Range", "THIS_WEEK");
        } else if (msg.contains("last week")) {
            filters.put(fieldPrefix + "Range", "LAST_WEEK");
        } else if (msg.contains("this month")) {
            filters.put(fieldPrefix + "Range", "THIS_MONTH");
        } else if (msg.contains("last month")) {
            filters.put(fieldPrefix + "Range", "LAST_MONTH");
        } else if (msg.contains("this year")) {
            filters.put(fieldPrefix + "Range", "THIS_YEAR");
        }
    }

    private String extractLocation(String original, String msg) {
        String location = extractAfterKeyword(original, msg, "in");
        if (location == null) {
            location = extractAfterKeyword(original, msg, "from");
        }

        if (location == null) {
            location = extractAfterKeyword(original, msg, "location");
        }

        return location == null ? null : cleanValue(removeKnownTrailingWords(location));
    }

    private String extractAfterKeyword(String original, String normalizedMsg, String keyword) {
        String keywordLower = keyword.toLowerCase();
        int idx = normalizedMsg.indexOf(keywordLower);

        if (idx == -1) return null;

        String extracted = original.substring(Math.min(original.length(), idx + keyword.length())).trim();

        if (extracted.isBlank()) return null;

        return extracted;
    }

    private String extractRegex(String input, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        if (matcher.find()) {
            return matcher.group(matcher.groupCount()).trim();
        }
        return null;
    }

    private String cleanValue(String value) {
        if (value == null) return null;

        return value
                .replace("?", "")
                .replace(".", "")
                .replace(",", "")
                .replace("page", "")
                .replace("please", "")
                .trim();
    }

    private String removeKnownTrailingWords(String value) {
        if (value == null) return null;

        return value
                .replaceAll("(?i)\\bexperience\\b.*", "")
                .replaceAll("(?i)\\byears\\b.*", "")
                .replaceAll("(?i)\\byear\\b.*", "")
                .replaceAll("(?i)\\bprofiles\\b.*", "")
                .replaceAll("(?i)\\bdemands\\b.*", "")
                .replaceAll("(?i)\\bresources\\b.*", "")
                .replaceAll("(?i)\\bwith\\b.*", "")
                .replaceAll("(?i)\\band\\b.*", "")
                .trim();
    }
}