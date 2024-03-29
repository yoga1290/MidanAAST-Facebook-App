package yoga1290.printk;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.omg.CORBA_2_3.portable.OutputStream;

/**
 * 
 * @author yoga1290
 * @see https://developers.facebook.com/docs/authentication/server-side/
 * @see https://developers.facebook.com/docs/authentication/permissions/
 * @see https://developers.facebook.com/docs/opengraph/using-app-tokens/
 */
public class facebook 
{
		/*
		 * Direct to:
		 https://www.facebook.com/dialog/oauth?client_id=187627904695576&redirect_uri=http://yoga1290.appspot.com/oauth/facebook/callback/&scope=user_about_me,email,publish_stream&state=SOME_ARBITRARY_BUT_UNIQUE_STRING
		 */
//		private String CLIENT_ID="187627904695576",CLIENT_SECRET="e35d3d770c34bf5c2204177015d1b28e";

		public static String getAccessToken(String CLIENT_ID,String CLIENT_SECRET,String code)
		{
			String res="";
			try
			{
//				https://graph.facebook.com/oauth/access_token?client_id=187627904695576&redirect_uri=http://yoga1290.appspot.com/oauth/facebook/callback/&client_secret=e35d3d770c34bf5c2204177015d1b28e&code=
				URL url = new URL("https://graph.facebook.com/oauth/access_token?client_id="+CLIENT_ID
								+"&redirect_uri=http://yoga1290.appspot.com/oauth/facebook/callback/"
								+"&client_secret="+CLIENT_SECRET
								+"&code="+code);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            InputStream in=connection.getInputStream();
	            byte buff[]=new byte[in.available()];
	            int ch;
	            while((ch=in.read(buff))!=-1)
	            		res+=new String(buff,0,ch);
	            in.close();
	            
	            //Extract the access token
	            return res.substring(res.indexOf("access_token=")+13,res.indexOf("&expires="));
			}catch(Exception e){
				res=res+" <br>Error: "+e.getMessage();
			}
			return res;
		}
		public static String getUser(String access_token)
		{
			String res="";
			try
			{
				URL url = new URL("https://graph.facebook.com/me?access_token="+access_token);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        InputStream in=connection.getInputStream();
		        byte buff[]=new byte[in.available()];
	            int ch;
	            while((ch=in.read(buff))!=-1)
	            		res+=new String(buff,0,ch);
		        in.close();
			}catch(Exception e){
				res=e.getMessage();
			}
			return res;
		}
		public static String extractJSON(String feild,String txt)
		{
			try{
				String tmp=txt.substring(txt.indexOf(feild)+feild.length()+3,txt.length());
				return txt.substring(txt.indexOf(feild)+feild.length()+3,tmp.indexOf("\"")+txt.indexOf(feild)+feild.length()+3);
			}catch(Exception e){
				return e.getMessage();
			}
		}
		
		/**
		 * 
		 * @param access_token access token with a friends_about_me permission 
		 * @return
		 */
		public static String[] getFriendsID(String access_token)
		{
			String txt="";
			String fin[];
			try
			{
				URL url = new URL("https://graph.facebook.com/me/friends?access_token="+access_token);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        InputStream in=connection.getInputStream();
		        byte buff[]=new byte[in.available()];
	            int ch;
	            while((ch=in.read(buff))!=-1)
	            		txt+=new String(buff,0,ch);
		        in.close();
		        String tmp[]=txt.split("},");
		        fin=new String[tmp.length];
		        for(int i=0;i<tmp.length;i++)
		        		fin[i]=extractJSON("id", tmp[i]);
			}catch(Exception e){
				fin=new String[]{e.getMessage()};
			}
			return fin;
		}
		
		
		/**
		 * 
		 * @param access_token access token with a publish_stream permission
		 * @param userID
		 * @param message
		 * @return
		 */
		public static String post(String access_token,String userID,String message)
		{
			String res="";
			try
			{	
				URL url = new URL("https://graph.facebook.com/"+userID+"/feed?access_token="+access_token);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoOutput(true);
		        connection.setRequestMethod("POST");
		
		        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		        writer.write("message="+message);
		        writer.close();
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
		
		
		public static String postPhoto(String access_token,String imgURL)
		{
			String res="";
			try
			{	
				URL url = new URL("https://graph.facebook.com/me/photos?access_token="+access_token);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoOutput(true);
		        connection.setRequestMethod("POST");
		        
		        connection.setRequestProperty("message", "Uploaded by apps.facebook.com/simplegraph");
		        connection.setRequestProperty("source", imgURL);
//		        java.io.OutputStream out = connection.getOutputStream();
////		        out.write(("message=Uploaded by Simple Graph! \r\n\r".getBytes()));
//		        out.write(("source=".getBytes()));
////		        connection.setRequestProperty("source", data);
//		        out.write(data);
//		        out.close();
		        
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
		
		public static String postNotification(String access_token,String user_id,String message,String template,String href)
		{
			///{recipient_userid}/notifications?access_token= � &template= � &href= �
			String res="";
			try
			{	
				URL url = new URL("https://graph.facebook.com/"+user_id+"/notifications?access_token="+access_token+"&template="+template+"&href="+href);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoOutput(true);
		        connection.setRequestMethod("POST");
		        
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
	}
