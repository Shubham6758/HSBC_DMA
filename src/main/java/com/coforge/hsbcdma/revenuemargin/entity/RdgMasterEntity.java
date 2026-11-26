package com.coforge.hsbcdma.revenuemargin.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_rdg_master")
public class RdgMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employee_no")
    private String employeeNo;

    @Column(name = "base_employee_no")
    private String baseEmployeeNo;

    private String name;
    private String email;

    @Column(name = "employee_type")
    private String employeeType;

    @Column(name = "employee_sub_group")
    private String employeeSubGroup;

    @Column(name = "trainee_category")
    private String traineeCategory;

    private String gender;

    @Column(name = "onsite_offshore")
    private String onsiteOffshore;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "company_name")
    private String companyName;

    private String country;

    @Column(name = "pa_code")
    private String paCode;

    @Column(name = "pa_name")
    private String paName;

    private String psa;

    @Column(name = "role_code")
    private String roleCode;

    @Column(name = "role_description")
    private String roleDescription;

    private String designation;

    @Column(name = "designation_level")
    private String designationLevel;

    @Column(name = "role_band")
    private String roleBand;

    private String dis;
    private String disp;
    private String classifier;

    @Column(name = "emp_practice")
    private String empPractice;

    @Column(name = "employee_group_practice")
    private String employeeGroupPractice;

    @Column(name = "supervisor_empcode")
    private String supervisorEmpcode;

    @Column(name = "supervisor_name")
    private String supervisorName;

    @Column(name = "career_manager_code")
    private String careerManagerCode;

    @Column(name = "career_manager_name")
    private String careerManagerName;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "organizational_unit")
    private String organizationalUnit;

    @Column(name = "organizational_unit_name")
    private String organizationalUnitName;

    @Column(name = "ipms_parent_wbs_code")
    private String ipmsParentWbsCode;

    @Column(name = "ipms_parent_wbs_name")
    private String ipmsParentWbsName;

    @Column(name = "ipms_wbs_code")
    private String ipmsWbsCode;

    @Column(name = "ipms_wbs_name")
    private String ipmsWbsName;

    @Column(name = "wbs_type")
    private String wbsType;

    @Column(name = "ipms_allocation_from_date")
    private LocalDate ipmsAllocationFromDate;

    @Column(name = "ipms_allocation_to_date")
    private LocalDate ipmsAllocationToDate;

    @Column(name = "forced_allocation")
    private String forcedAllocation;

    @Column(name = "cost_centre")
    private String costCentre;

    @Column(name = "cost_centre_name")
    private String costCentreName;

    @Column(name = "project_practice_code")
    private String projectPracticeCode;

    @Column(name = "project_practice")
    private String projectPractice;

    @Column(name = "project_group_practice")
    private String projectGroupPractice;

    private String ibu;

    @Column(name = "sub_ibu")
    private String subIbu;

    @Column(name = "emp_horizontal")
    private String empHorizontal;

    @Column(name = "project_horizontal")
    private String projectHorizontal;

    @Column(name = "cost_group")
    private String costGroup;

    @Column(name = "cost_group_desc")
    private String costGroupDesc;

    @Column(name = "bu_cost_group")
    private String buCostGroup;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "account_customer_group")
    private String accountCustomerGroup;

    @Column(name = "prev_employee_code")
    private String prevEmployeeCode;

    @Column(name = "prev_doj")
    private LocalDate prevDoj;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "skill_area")
    private String skillArea;

    @Column(name = "skill_cluster")
    private String skillCluster;

    @Column(name = "sub_cluster")
    private String subCluster;

    @Column(name = "provisional_ibu")
    private String provisionalIbu;

    @Column(name = "provisional_sub_bu")
    private String provisionalSubBu;

    @Column(name = "billed_unbilled")
    private String billedUnbilled;

    @Column(name = "it_bps_hc")
    private String itBpsHc;

    private String geo;
    private String location;

    @Column(name = "org_key")
    private String orgKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getBaseEmployeeNo() {
        return baseEmployeeNo;
    }

    public void setBaseEmployeeNo(String baseEmployeeNo) {
        this.baseEmployeeNo = baseEmployeeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getEmployeeSubGroup() {
        return employeeSubGroup;
    }

    public void setEmployeeSubGroup(String employeeSubGroup) {
        this.employeeSubGroup = employeeSubGroup;
    }

    public String getTraineeCategory() {
        return traineeCategory;
    }

    public void setTraineeCategory(String traineeCategory) {
        this.traineeCategory = traineeCategory;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOnsiteOffshore() {
        return onsiteOffshore;
    }

    public void setOnsiteOffshore(String onsiteOffshore) {
        this.onsiteOffshore = onsiteOffshore;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPaCode() {
        return paCode;
    }

    public void setPaCode(String paCode) {
        this.paCode = paCode;
    }

    public String getPaName() {
        return paName;
    }

    public void setPaName(String paName) {
        this.paName = paName;
    }

    public String getPsa() {
        return psa;
    }

    public void setPsa(String psa) {
        this.psa = psa;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDesignationLevel() {
        return designationLevel;
    }

    public void setDesignationLevel(String designationLevel) {
        this.designationLevel = designationLevel;
    }

    public String getRoleBand() {
        return roleBand;
    }

    public void setRoleBand(String roleBand) {
        this.roleBand = roleBand;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public String getDisp() {
        return disp;
    }

    public void setDisp(String disp) {
        this.disp = disp;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getEmpPractice() {
        return empPractice;
    }

    public void setEmpPractice(String empPractice) {
        this.empPractice = empPractice;
    }

    public String getEmployeeGroupPractice() {
        return employeeGroupPractice;
    }

    public void setEmployeeGroupPractice(String employeeGroupPractice) {
        this.employeeGroupPractice = employeeGroupPractice;
    }

    public String getSupervisorEmpcode() {
        return supervisorEmpcode;
    }

    public void setSupervisorEmpcode(String supervisorEmpcode) {
        this.supervisorEmpcode = supervisorEmpcode;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getCareerManagerCode() {
        return careerManagerCode;
    }

    public void setCareerManagerCode(String careerManagerCode) {
        this.careerManagerCode = careerManagerCode;
    }

    public String getCareerManagerName() {
        return careerManagerName;
    }

    public void setCareerManagerName(String careerManagerName) {
        this.careerManagerName = careerManagerName;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public String getOrganizationalUnitName() {
        return organizationalUnitName;
    }

    public void setOrganizationalUnitName(String organizationalUnitName) {
        this.organizationalUnitName = organizationalUnitName;
    }

    public String getIpmsParentWbsCode() {
        return ipmsParentWbsCode;
    }

    public void setIpmsParentWbsCode(String ipmsParentWbsCode) {
        this.ipmsParentWbsCode = ipmsParentWbsCode;
    }

    public String getIpmsParentWbsName() {
        return ipmsParentWbsName;
    }

    public void setIpmsParentWbsName(String ipmsParentWbsName) {
        this.ipmsParentWbsName = ipmsParentWbsName;
    }

    public String getIpmsWbsCode() {
        return ipmsWbsCode;
    }

    public void setIpmsWbsCode(String ipmsWbsCode) {
        this.ipmsWbsCode = ipmsWbsCode;
    }

    public String getIpmsWbsName() {
        return ipmsWbsName;
    }

    public void setIpmsWbsName(String ipmsWbsName) {
        this.ipmsWbsName = ipmsWbsName;
    }

    public String getWbsType() {
        return wbsType;
    }

    public void setWbsType(String wbsType) {
        this.wbsType = wbsType;
    }

    public LocalDate getIpmsAllocationFromDate() {
        return ipmsAllocationFromDate;
    }

    public void setIpmsAllocationFromDate(LocalDate ipmsAllocationFromDate) {
        this.ipmsAllocationFromDate = ipmsAllocationFromDate;
    }

    public LocalDate getIpmsAllocationToDate() {
        return ipmsAllocationToDate;
    }

    public void setIpmsAllocationToDate(LocalDate ipmsAllocationToDate) {
        this.ipmsAllocationToDate = ipmsAllocationToDate;
    }

    public String getForcedAllocation() {
        return forcedAllocation;
    }

    public void setForcedAllocation(String forcedAllocation) {
        this.forcedAllocation = forcedAllocation;
    }

    public String getCostCentre() {
        return costCentre;
    }

    public void setCostCentre(String costCentre) {
        this.costCentre = costCentre;
    }

    public String getCostCentreName() {
        return costCentreName;
    }

    public void setCostCentreName(String costCentreName) {
        this.costCentreName = costCentreName;
    }

    public String getProjectPracticeCode() {
        return projectPracticeCode;
    }

    public void setProjectPracticeCode(String projectPracticeCode) {
        this.projectPracticeCode = projectPracticeCode;
    }

    public String getProjectPractice() {
        return projectPractice;
    }

    public void setProjectPractice(String projectPractice) {
        this.projectPractice = projectPractice;
    }

    public String getProjectGroupPractice() {
        return projectGroupPractice;
    }

    public void setProjectGroupPractice(String projectGroupPractice) {
        this.projectGroupPractice = projectGroupPractice;
    }

    public String getIbu() {
        return ibu;
    }

    public void setIbu(String ibu) {
        this.ibu = ibu;
    }

    public String getSubIbu() {
        return subIbu;
    }

    public void setSubIbu(String subIbu) {
        this.subIbu = subIbu;
    }

    public String getEmpHorizontal() {
        return empHorizontal;
    }

    public void setEmpHorizontal(String empHorizontal) {
        this.empHorizontal = empHorizontal;
    }

    public String getProjectHorizontal() {
        return projectHorizontal;
    }

    public void setProjectHorizontal(String projectHorizontal) {
        this.projectHorizontal = projectHorizontal;
    }

    public String getCostGroup() {
        return costGroup;
    }

    public void setCostGroup(String costGroup) {
        this.costGroup = costGroup;
    }

    public String getCostGroupDesc() {
        return costGroupDesc;
    }

    public void setCostGroupDesc(String costGroupDesc) {
        this.costGroupDesc = costGroupDesc;
    }

    public String getBuCostGroup() {
        return buCostGroup;
    }

    public void setBuCostGroup(String buCostGroup) {
        this.buCostGroup = buCostGroup;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAccountCustomerGroup() {
        return accountCustomerGroup;
    }

    public void setAccountCustomerGroup(String accountCustomerGroup) {
        this.accountCustomerGroup = accountCustomerGroup;
    }

    public String getPrevEmployeeCode() {
        return prevEmployeeCode;
    }

    public void setPrevEmployeeCode(String prevEmployeeCode) {
        this.prevEmployeeCode = prevEmployeeCode;
    }

    public LocalDate getPrevDoj() {
        return prevDoj;
    }

    public void setPrevDoj(LocalDate prevDoj) {
        this.prevDoj = prevDoj;
    }

    public LocalDate getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getSkillArea() {
        return skillArea;
    }

    public void setSkillArea(String skillArea) {
        this.skillArea = skillArea;
    }

    public String getSkillCluster() {
        return skillCluster;
    }

    public void setSkillCluster(String skillCluster) {
        this.skillCluster = skillCluster;
    }

    public String getSubCluster() {
        return subCluster;
    }

    public void setSubCluster(String subCluster) {
        this.subCluster = subCluster;
    }

    public String getProvisionalIbu() {
        return provisionalIbu;
    }

    public void setProvisionalIbu(String provisionalIbu) {
        this.provisionalIbu = provisionalIbu;
    }

    public String getProvisionalSubBu() {
        return provisionalSubBu;
    }

    public void setProvisionalSubBu(String provisionalSubBu) {
        this.provisionalSubBu = provisionalSubBu;
    }

    public String getBilledUnbilled() {
        return billedUnbilled;
    }

    public void setBilledUnbilled(String billedUnbilled) {
        this.billedUnbilled = billedUnbilled;
    }

    public String getItBpsHc() {
        return itBpsHc;
    }

    public void setItBpsHc(String itBpsHc) {
        this.itBpsHc = itBpsHc;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrgKey() {
        return orgKey;
    }

    public void setOrgKey(String orgKey) {
        this.orgKey = orgKey;
    }
}