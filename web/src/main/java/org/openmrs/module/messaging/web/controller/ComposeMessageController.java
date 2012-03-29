package org.openmrs.module.messaging.web.controller;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessageService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.omail.OMailProtocol;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ComposeMessageController {
	
	private boolean replyingTo=false;
	@RequestMapping(value = "/module/messaging/compose_message")
	public void populateModel(HttpServletRequest request){
		HttpSession session = request.getSession();
		if(!replyingTo){
			session.setAttribute("toAddresses", "");
			session.setAttribute("subject", "");
			session.setAttribute("messageText", "");
		}else{
			replyingTo=false;
		}
	}
	
	@RequestMapping(value="/module/messaging/reply_to_message", method=RequestMethod.POST)
	public ModelAndView search(@RequestParam("replyToMessageId") Integer messageId,
							   @RequestParam("replyAll") boolean replyAll,
							   HttpServletRequest request){
		HttpSession session = request.getSession();
		Message m = Context.getService(MessageService.class).getMessage(messageId);
		String addresses = "";
		addresses += "\"" + m.getSender().getPersonName().toString()+ "\"";
		addresses += " <omail:"+ m.getSender().getId().toString() + ">, ";
		if(replyAll){
			for(MessageRecipient mr: m.getTo()){
				if(mr.getProtocol().equals(OMailProtocol.class)){
					addresses += "\"" + mr.getRecipient().getPerson().getPersonName().toString()+ "\"";
					addresses += " <omail:"+ mr.getRecipient().getPerson().getId().toString() + ">, ";
				}
			}
		}
		String subject = "Re: "+ m.getSubject();
		String message = "<div style=\"border-left:1px solid #104E8B; padding-left:5px;color:#104E8B\">" + m.getContent() + "</div><br>" ;
		String messageHeader = "<br><br>On " + Context.getDateFormat().format(m.getDate())+
								" at " + new SimpleDateFormat("h:mm a").format(m.getDate()) + ", " + 
								m.getSender().getPersonName().toString() + " wrote:<br>";
		message = messageHeader + message;
		session.setAttribute("toAddresses", addresses);
		session.setAttribute("subject", subject);
		session.setAttribute("messageText", message);
		replyingTo=true;
		return new ModelAndView(new RedirectView("compose_message.form"));
	}
}
