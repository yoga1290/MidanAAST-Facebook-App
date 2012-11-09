package yoga1290.printk;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

// https://www.facebook.com/dialog/oauth?client_id=515920055103374&redirect_uri=http://yoga1290.appspot.com/oauth/facebook/callback/&scope=user_about_me,email,publish_stream,friends_about_me,user_education_history&state=meedan
public class meedan 
{
	public static void direct(String uri)
	{
		
	}
	public static String saveMember(String access_token)
	{
		String tmp="";
		try {
			int i;
			JSONObject json=new JSONObject(facebook.getUser(access_token));
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			

			com.google.appengine.api.datastore.Transaction txn = datastore.beginTransaction();
			String fbid=json.getString("id");
			Entity member;
			
			String cur="unkown";
			Entity school=null,concentration=null;
			JSONArray schools=json.getJSONArray("education");//schools
				try{
					cur=schools.getJSONObject(schools.length()-1).getJSONObject("school").getString("name");
					if(cur==null)
						cur="unkown";
					school=datastore.get(KeyFactory.createKey("midan_school", cur));
					
					if(school!=null)
						school.setProperty("count",
								(Integer.parseInt((String)school.getProperty("count"))+1)+"" );
					else
					{
						school = new Entity("midan_school",
								cur);
							school.setProperty("count", "1");
					}
					datastore.put(school);
				}catch(Exception e){
					school = new Entity("midan_school",
						cur);
					school.setProperty("count", "1");
					datastore.put(school);
				}
				tmp+="School:"+cur+"<br>";

				
				cur="unkown";
				
				
				concentration=null;
					try{
						JSONArray con=schools.getJSONObject(schools.length()-1).optJSONArray("concentration");
						cur=con.getJSONObject(con.length()-1).getString("name");//j).getString("name");
						if(cur==null)
							cur="unkown";
						concentration=datastore.get(KeyFactory.createKey("midan_major", cur ) );
						if(concentration!=null)
							concentration.setProperty("count",
									(Integer.parseInt((String)concentration.getProperty("count"))+1)+"" );
						else
						{
							concentration=new Entity("midan_major", cur,school.getKey());
							concentration.setProperty("count", "1");
						}
						datastore.put(concentration);
						
					}catch(Exception e){
						concentration=new Entity("midan_major", cur,school.getKey());
						concentration.setProperty("count", "1");
						datastore.put(concentration);
					}
					
					tmp+="Major:"+cur+"<br>";
					
				if(concentration!=null)
					member = new Entity("midan",	json.getString("email"),	concentration.getKey());
				else
					member = new Entity("midan",	json.getString("email"),	school.getKey());
				member.setProperty("name", 	json.getString("name"));
				member.setProperty("fbid",	fbid);
				member.setProperty("access_token",	access_token);
			datastore.put(member);
			txn.commit();
			return "Thanks for joinning "+json.getString("name")+" <br> I'm working on stat about schools vs majors...<br>"+tmp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return tmp+"<br>"+e.getMessage();
		}
	}
	
	public static String getSchoolStat()
	{
		Query q = new Query("midan_school");
		PreparedQuery pq = DatastoreServiceFactory.getDatastoreService().prepare(q);
		int cnt=pq.countEntities(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(10000));
		List<Entity> res= pq.asList(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1));
		String txt="";
		for(int i=0;i<cnt;i++)
		{
			Entity cur=res.get(i);
			txt+=cur.getKey().getName()+"\n"+cur.getProperty("count");
		}
		return txt;
	}
	public static String getMajorStat()
	{
		Query q = new Query("midan_major");
		PreparedQuery pq = DatastoreServiceFactory.getDatastoreService().prepare(q);
		int cnt=pq.countEntities(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(10000));
		List<Entity> res= pq.asList(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
		String txt="";
		for(int i=0;i<cnt;i++)
		{
			Entity cur=res.get(i);
			txt+=cur.getParent().getName()+"\n"+cur.getKey().getName()+"\n"+((String)cur.getProperty("count"))+"\n";
		}
		return txt;
	}
	public static String getCSV()
	{
		Query q = new Query("midan");
		PreparedQuery pq = DatastoreServiceFactory.getDatastoreService().prepare(q);
		int cnt=pq.countEntities(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
		List<Entity> res= pq.asList(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
		String txt="";
		for(int i=0;i<cnt;i++)
		{
			Entity cur=res.get(i);
			txt+="\""+cur.getProperty("name")+"\",\""+cur.getKey().getName()+"\","+cur.getProperty("fbid")+",\""+cur.getParent().getName()+"\",\""+cur.getParent().getParent().getName()+"\"\n";
		}
		return txt;
	}
	public static String sendNotifications(String message)
	{
		String txt="";
		Query q = new Query("midan");
		PreparedQuery pq = DatastoreServiceFactory.getDatastoreService().prepare(q);
		int cnt=pq.countEntities(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
		List<Entity> res= pq.asList(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
		for(int i=0;i<cnt;i++)
		{
			if(((String)res.get(i).getProperty("email")).equals("yoga1290@gmail.com"))
				txt+=facebook.postNotification((String)res.get(i).getProperty("access_token"), (String)res.get(i).getProperty("fbid"), message, "", "http://apps.facebook.com/midanclub");
		}
		return txt;
	}
	
}
