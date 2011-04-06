package org.openmrs.module.messaging.web.domain;

import java.util.List;

public class MessageBeanSet {

	/**
	 * The messages in this set
	 */
	private List<MessageBean> messages;
		
	/**
	 * The page number that we are on, starting from 0
	 */
	private Integer pageNumber;

	/**
	 * The pagesize, assumed to always be 10.
	 * At least until we add that preference.
	 */
	private Integer pageSize = 10;
	
	/**
	 * The total number of messages that can be paged through
	 */
	private Integer total;

	public MessageBeanSet(List<MessageBean> messages, Integer total, Integer pageNumber){
		this.setMessages(messages);
		this.setTotal(total);
		this.setPageNumber(pageNumber);
	}
	
	public Integer getStart(){
		return (getPageNumber() * getPageSize()) + 1;
	}
	
	public Integer getEnd(){
		return (getPageNumber() * getPageSize()) + getMessages().size();
	}
	
	public Integer getTotal(){
		return total;
	}
	
	public List<MessageBean> getMessages(){
		return messages;
	}

	/**
	 * @return the pageNumber
	 */
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	/**
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(List<MessageBean> messages) {
		this.messages = messages;
	}

	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
}
