package controller.support;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * The base action for list, edit, save and delete (LESD) actions.
 * 
 */
public abstract class BaseLesdAction extends BaseAction {
	private static Log log = LogFactory.getLog(BaseLesdAction.class);
	
	// The id of the business object (a.k.a. model) we are working with
	protected Long id;
	// Flag to indicate if the delete button was pressed
	private String deleteClick;

	protected String printClick;

	public BaseLesdAction() {
		super();
	}

	public abstract String list();

	public abstract String edit();

	/**
	 * The form submits to the post method who figures out if this was a save or
	 * delete.
	 */
	public String post() {
		if (deleteClick != null) {
			log.info("Deleting id:" + id);
			return delete();
		}else if (printClick != null && !"".equals(printClick)) {
			printClick = null;
			log.info("Printing id:" + id);
			return print();
		} else {
			log.info("Saving id:" + id);
			return save();
		}
	}

	public abstract String save();
	
	public String print(){
		return ITEXT;
	}

	public abstract String delete();


	// Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeleteClick() {
		return deleteClick;
	}

	public void setDeleteClick(String delete) {
		this.deleteClick = delete;
	}

	public String getPrintClick() {
		return printClick;
	}

	public void setPrintClick(String printClick) {
		this.printClick = printClick;
	}

}