import java.net.URLEncoder;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class WebScraper {

    public static void main(String[] args) throws InterruptedException {


        String searchQuery = "jeff bezos" ;
        String baseUrl = "https://nitter.net/search?f=tweets&q=";
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        try {
            String searchUrl = baseUrl + URLEncoder.encode(searchQuery, "UTF-8");
            HtmlPage page = client.getPage(searchUrl);

            List<HtmlElement> items = page.getByXPath("//div[@class='timeline-item ']") ;
            if(items.isEmpty()){
                System.out.println("No items found !");
            }else{
                for(HtmlElement htmlItem : items){

                    HtmlElement spanBody = htmlItem.getFirstByXPath(".//div[@class='tweet-body']");

                    Item item = new Item();

                    item.setBody(spanBody.asNormalizedText());
                    
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = mapper.writeValueAsString(item);
                    insertTweet(jsonString);

                    System.out.println(jsonString);
                }

            }
        } catch(Exception e){
            e.printStackTrace();
        }

        Thread.sleep(15000);
    }

        static MongoUtil mongoUtil = new MongoUtil();
        static MongoDatabase database = mongoUtil.getDB();
        static MongoCollection<Document> collection = database.getCollection("body");
        public static void insertTweet(String result) throws InterruptedException
    {
        Document document = new Document("body", result);
        mongoUtil.getUserCollection().insertOne(document);
    }
}