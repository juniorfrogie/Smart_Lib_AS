package com.webcash.sws.comm.tx.push;

public class GcmListener {
	
	/*****************************
	 * 등록 관련 인터페이스
	 ****************************/	
	public interface onRegistrationListener {

        /**
         * 등록 성공시 리턴받은 regId를 자신이 구현한 3rd-party server에 알려준다.
         */
		public void onRegistrationCompleted(String regid);
		
		/**
		 * 등록과정에서 오류 발생시
		 * @param error_cd : 오류 코드
		 * @param error_msg : 오류 메시지
		 */
		public void onRegistrationError(String error_cd, String error_msg);
	    
	}
	
	
	/*****************************
	 * 폐기 관련 인터페이스
	 ****************************/
	
	public interface onUnRegistrationListener {

        /**
         * 폐기 성공시 
         */
		public void onUnRegistrationCompleted();
		
		/**
		 * 폐기 과정에서 오류발생시
		 * @param error_cd : 오류 코드
		 * @param error_msg : 오류 메시지
		 */
		public void onUnRegistrationError(String error_cd, String error_msg);
	    
	}
	
	/*****************************
	 * 메세지 수신 관련 인터페이스
	 ****************************/
	public interface onSendMessageListener {
		public void onSendMessageCompleted(int msgid);		
	}
    
}
