package org.openmrs.module.messaging.domain;

public enum MessageStatus{
		SENT(0),
		RECEIVED(1),
		RETRYING(2),
		FAILED(3),
		OUTBOX(4);
		
		private int number;
		
		private MessageStatus(int number){
			this.number = number;
		}
		
		public static MessageStatus getStatusByNumber(int number){
			for(MessageStatus m: MessageStatus.values()){
				if(m.getNumber() == number){
					return m;
				}
			}
			return null;
		}
		
		/**
		 * @return the number
		 */
		public int getNumber() {
			return number;
		}	
}