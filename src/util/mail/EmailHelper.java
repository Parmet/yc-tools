package util.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.dom4j.Document;

import util.xml.XmlUtil;

/**
 * 邮件操作类(不过目前只能在内部发送)
 * 
 * @author chenys 2012.02.20
 * 
 */
public class EmailHelper {

	/**
	 * 设置smtp服务
	 * 
	 * @param host
	 *            服务器名
	 * @param port
	 *            服务端口
	 * @param auth
	 *            是否身份验证
	 * @return
	 */
	public Properties setHost(String host, String port, String auth) {
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", auth);
		return props;
	}

	/**
	 * 设置邮件目标地址
	 * 
	 * @param message
	 *            邮件信息对象
	 * @param address
	 *            目标地址,以分号隔开
	 * @param iAddressType
	 *            地址类型,分接收人/抄送/暗送
	 */
	public void setAddress(Message message, String address,
			RecipientType addressType) {
		try {
			if (null != address && ("").equals(address)) {
				ArrayList<String> alAddress = this.splitStr(address, ';');
				Address[] toAddress = new InternetAddress[alAddress.size()];
				for (int i = 0; i < alAddress.size(); i++) {
					toAddress[i] = new InternetAddress((String) alAddress
							.get(i));
				}
				message.addRecipients(addressType, toAddress);
			}
		} catch (AddressException ae) {
			ae.printStackTrace();
		} catch (MessagingException me) {
			me.printStackTrace();
		} finally {
			// TODO
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @param xmlPath
	 *            邮件服务器xml配置文件路径
	 * @param emailTo
	 *            收件人
	 * @param emailCC
	 *            抄送人
	 * @param emailBCC
	 *            密件抄送
	 * @param emailTitle
	 *            主题
	 * @param emailContent
	 *            内容
	 * @param attachment
	 *            附件
	 * @param isHtml
	 *            是否以Html方式发送
	 * @return
	 */
	@SuppressWarnings("static-access")
	public boolean sendEmail(String xmlPath, String toAddress,
			String ccAddress, String bccAddress, String title, String content,
			ArrayList<String> attachment, boolean isHtml) {
		try {
			// 检查xml文件是否存在
			File file = new File(xmlPath);
			if (!file.isFile()) {
				throw new FileNotFoundException("系统未找到邮件服务的XML配置文件");
			}
			// 检查文件是否为xml文件
			String fileExt = xmlPath.substring(xmlPath.lastIndexOf("."))
					.toLowerCase();
			if (!fileExt.equals("xml")) {
				throw new FileNotFoundException("配置文件必须是xml文件");
			}

			// 解析xml文件,读取其中配置
			Document doc = XmlUtil.getDocument(file, "utf-8");
			// SMTP服务器名
			String host = XmlUtil.getElementValue(doc, "/email/host");
			// 端口号,默认25
			String port = XmlUtil.getElementValue(doc, "/email/port");
			// 默认发送者
			String from = XmlUtil.getElementValue(doc, "/email/from");
			// 用户名
			String user = XmlUtil.getElementValue(doc, "/email/user");
			// 密码
			String password = XmlUtil.getElementValue(doc, "/email/password");
			// 是否验证身份
			String auth = XmlUtil.getElementValue(doc, "/email/auth");

			System.out.println("smtp名称:" + host);
			System.out.println("smtp端口:" + port);
			System.out.println("发送者地址:" + from);
			System.out.println("用户名:" + user);
			System.out.println("密码:" + password);
			System.out.println("是否身份验证:" + auth);

			// 验证xml配置是否完整
			if ((host == null || host.equals(""))
					|| (port == null || port.equals(""))
					|| (from == null || from.equals(""))
					|| (user == null || user.equals(""))
					|| (password == null || password.equals(""))
					|| (auth == null || auth.equals(""))) {
				throw new Exception("smtp服务器的xml文件配置不完整");
			}

			// 验证xml数据格式
			Pattern p = null;
			Matcher m = null;
			// smtp服务器名称
			p = Pattern.compile("\\w+.\\w+.[a-zA-Z]{2,3}(.\\w{2})?");
			m = p.matcher(host);
			if (!m.matches()) {
				throw new Exception(
						"smtp服务器名称格式错误.格式如:smtp.163.com或者smtp.163.com.cn");
			}
			// 端口号
			p = Pattern.compile("[\\d]+");
			m = p.matcher(port);
			if (!m.matches()) {
				throw new Exception("端口号需为数字格式");
			}
			// 发件人
			p = Pattern.compile("\\w+@(\\w+.)+[a-zA-Z]{2,3}(.\\w{2})?");
			m = p.matcher(from);
			if (!m.matches()) {
				throw new Exception("发件人邮箱地址格式错误");
			}
			// 身份验证
			p = Pattern.compile("true|false");
			m = p.matcher(auth);
			if (!m.matches()) {
				throw new Exception("是否身份验证(auth)的值需输入true或者false");
			}
			// 对比服务域名与发件人域名
			String hostExt = host.substring(host.indexOf(".", 0));
			String fromExt = from.substring(from.indexOf("@"));
			if (!hostExt.equalsIgnoreCase(fromExt)) {
				throw new Exception("发件人地址域名必须与smtp服务器名称的域名保持一致");
			}

			// 设置smtp服务器
			Properties props = setHost(host, port, auth);
			// 设置smtp服务身份验证功能
			Authenticator authenticator = null;
			if (Boolean.parseBoolean(auth)) {
				authenticator = new EmailAuthenticator(user, password);
			}
			// 开启smtp服务会话
			Session session = Session.getDefaultInstance(props, authenticator);
			// 设置邮件内容
			Message message = new MimeMessage(session);
			// 标题
			message.setSubject(title);
			// 发送日期
			message.setSentDate(new Date());
			// 发件人
			message.setFrom(new InternetAddress(from));
			// 接收人
			setAddress(message, toAddress, RecipientType.TO);
			// 抄送人
			setAddress(message, ccAddress, RecipientType.CC);
			// 暗送
			setAddress(message, bccAddress, RecipientType.BCC);
			// 邮件内容
			MimeMultipart mp = new MimeMultipart();
			MimeBodyPart mbp = new MimeBodyPart();
			if (isHtml) {
				mbp.setContent(content, "text/html;charset=utf-8");
			} else {
				mbp.setText(content);
			}
			mp.addBodyPart(mbp);
			// 邮件附件
			if (attachment.size() > 0) {
				for (String s : attachment) {
					MimeBodyPart mdpAtt = new MimeBodyPart();
					// 添加附件
					FileDataSource fds = new FileDataSource(s);
					mdpAtt.setDataHandler(new DataHandler(fds));
					// 格式化附件名称
					mdpAtt.setFileName(MimeUtility.encodeText(new String(fds
							.getName().getBytes(), "GB2312"), "GB2312", "B"));
					mp.addBodyPart(mdpAtt);
				}
			}
			message.setContent(mp);
			message.saveChanges();

			// 发送邮件
			Transport transport = session.getTransport("smtp");
			transport.send(message);
			transport.close();

			System.out.println("邮件已发送成功!");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			// TODO
		}
	}

	/**
	 * 按照指定符号,将字符串分隔成集合
	 * 
	 * @param str
	 *            字符串
	 * @param sp
	 *            分隔符,默认为分号(;)
	 * @return
	 */
	public ArrayList<String> splitStr(String str, char sp) {
		ArrayList<String> result = new ArrayList<String>();
		String[] res = str.split("" + sp);
		for (String s : res) {
			if (null != s && !"".equals(s)) {
				result.add(s);
			}
		}
		return result;
	}
}
