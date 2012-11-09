import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


@SuppressWarnings("serial")
public class AppEngine extends HttpServlet {
	public String readFromURL(String uri)
	{
		String res="";
		try
		{	
			URL url = new URL(uri);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        InputStream in=connection.getInputStream();
	        byte buff[]=new byte[in.available()];
            int ch;
            while((ch=in.read(buff))!=-1)
            		res+=new String(buff,0,ch);
		}catch(Exception e)
		{
			res=e.getMessage();
		}
		return res;
	}
	public void emailMe(String msgBody)
	{
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);


        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("yoga1290@gmail.com", "Youssef Gamil"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress("yoga1290@gmail.com", "Youssef Gamil"));
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (Exception e) {
            // ...
        }
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
	}


	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
			resp.setContentType("text/html");
		
			if(req.getRequestURI().equals("/oauth/facebook/callback/"))
			{
				String code=req.getParameter("code");
					//MIDAN
					// https://www.facebook.com/dialog/oauth?client_id=515920055103374&redirect_uri=http://yoga1290.appspot.com/oauth/facebook/callback/&scope=user_about_me,email,user_education_history&state=midan
					if(req.getParameter("state").equals("midan")) //1st callback;callback for Auth code
					{
						try{
							String access_token=facebook.getAccessToken("515920055103374","***",code);
		//					resp.getWriter().println("access token="+access_token+"<br>");
							
//							resp.getWriter().println(
									meedan.saveMember(access_token);
//									);
									resp.getWriter().println(readFromURL("https://yoga1290.appspot.com/midan/index.html"));
//									resp.getWriter().println(readFromURL("https://yoga1290.appspot.com/meedan.html"));
						}catch(Exception e){resp.getWriter().println("Error:"+e.getMessage()+"\n :(");}
					}
					// additional permissions:
					// https://www.facebook.com/dialog/oauth?client_id=515920055103374&redirect_uri=http://yoga1290.appspot.com/oauth/facebook/callback/&scope=user_about_me,publish_stream,friends_about_me&state=midanpostwall
					else if(req.getParameter("state").equals("midanpostwall"))
					{
						String res="";
						try{
							String access_token=facebook.getAccessToken("515920055103374","ed37e4469f8a691b552717047e3a2a91",code);
							resp.getWriter().println("access token="+access_token+"<br>");
							JSONObject user=new JSONObject(facebook.getUser(access_token));
//							
							String friends[]=facebook.getFriendsID(access_token);
							for(int i=0;i<friends.length;i++)
								res+=facebook.post(access_token, friends[i], user.get("name")+" is inviting you to join the Midan club! \n http://on.fb.me/PJtyQQ for basic registeration! \n App canvas on http://apps.facebook.com/midanaast");
							resp.getWriter().println(":)\n");
//								facebook.post(access_token, (String)user.get("id"), user.get("name")+" just joined Midan club!\n http://apps.facebook.com/midanaast");
						}catch(Exception e){resp.getWriter().println(res+"\nError:"+e.getMessage()+"\n :(");}
					}
		else if(req.getRequestURI().substring(1,req.getRequestURI().lastIndexOf("/")).equals("tweets"))
		{
			try{
				String result="";
				String username=req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/")+1);
				JSONObject res=new JSONObject(readFromURL("http://search.twitter.com/search.json?q=from%3A"+username));
				JSONArray tweets=res.getJSONArray("results");
				for(int i=0;i<tweets.length();i++)
					result+=new String( ((String)tweets.getJSONObject(i).get("text")).replaceAll("\n", "<br>").getBytes(),"UTF8")+"\n";
				
				resp.setContentType("text/plain");
				resp.getWriter().println(result);
			}catch(Exception e){}
		}

	}
}
