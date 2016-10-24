package dao;

import hibernate.NoRecordsFoundException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import model.BaseCompanyModel;
import model.Company;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import security.LocalSecurityContext;
import security.SecurityContext;
import type.NullSafeEquals;

import common.UncheckedException;

public abstract class BaseCompanyDaoNew<T extends BaseCompanyModel, PK extends Serializable>
		extends BaseDaoNew<T, PK> {

	public BaseCompanyDaoNew(Class<T> persistentClass) {
		super(persistentClass);
	}


}
