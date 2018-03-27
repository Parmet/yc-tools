package util.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class EmailAuthenticator extends Authenticator {
	public String userName;
	public String password;

	public EmailAuthenticator(String _userName, String _password) {
		this.userName = _userName;
		this.password = _password;
	}

	protected PasswordAuthentication getPassworkAuthenticator() {
		return new PasswordAuthentication(userName, password);
	}
}
