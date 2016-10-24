package dao;

import model.Company;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyDao extends BaseDaoNew<Company, Long> {

	private PersonAdminDao personDao;

	public CompanyDao() {
		super(Company.class);
	}

	public boolean isExistingCompany(String companyName) {
		return !find(Restrictions.eq("name", companyName)).isEmpty();
	}

	public boolean isEmptyCompany(Company company) {
		return personDao.find(Restrictions.eq("company", company)).isEmpty();
	}

	public void setPersonDao(PersonAdminDao personDao) {
		this.personDao = personDao;
	}

}
