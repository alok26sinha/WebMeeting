package controller.support;

import java.util.List;

import javax.annotation.Resource;

import model.Company;
import model.Subscription;
import dao.CompanyDao;
import dao.SubscriptionDao;

@SuppressWarnings("serial")
public class CompanyAction extends BaseAdminLesdAction {

	public Company company;
	public List<Company> companies;
	public String companyName;
	public String productionUrl;
	public String testUrl;
	public List<Subscription> subscriptions;

	private CompanyDao companyDao;
	@Resource
	private SubscriptionDao subscriptionDao;
	
	@Override
	public String list() {
		checkCurrentUserIsAdmin();
		companies = companyDao.getAll();
		return LIST;
	}

	@Override
	public String edit() {
		checkCurrentUserIsAdmin();
		company = companyDao.load(id);
		companyName = company.name;
		subscriptions = subscriptionDao.getSubscripitions(company);
		return EDIT;
	}

	@Override
	public String save() {
		checkCurrentUserIsAdmin();
		if (isValid()) {
			company = companyDao.load(id);
			company.name = companyName;
			companyDao.save(company);
			companyName = "";
			return list();
		}
		return edit();
	}

	@Override
	public String delete() {
		checkCurrentUserIsAdmin();
		company = companyDao.load(id);
		if (!companyDao.isEmptyCompany(company)) {
			addActionError("Can not delete company: delete the users first");
		} else {
			companyDao.delete(company);
		}
		return list();
	}
	
	public String addNew () {
		checkCurrentUserIsAdmin();
		if (isValid()) {
			company = new Company();
			company.name = companyName;
			companyDao.save(company);
			companyName = "";
			addActionMessage("Company [" + companyName + "] addedd");
		}
		return list();
	}
	
	private boolean isValid () {
		if (companyName == null || "".equals(companyName.trim())) {
			addActionError("Comapny name must not be empty!");
			return false;
		}
		if (companyDao.isExistingCompany(companyName)) {
			addActionError("Company with this name already exists!");
			return false;
		}
		return true;
	}
	
	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getProductionUrl() {
		return productionUrl;
	}

	public void setProductionUrl(String productionUrl) {
		this.productionUrl = productionUrl;
	}

	public String getTestUrl() {
		return testUrl;
	}

	public void setTestUrl(String testUrl) {
		this.testUrl = testUrl;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	
}
