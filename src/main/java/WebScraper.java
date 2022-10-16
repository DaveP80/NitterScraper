import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class WebScraper {

    public static void main(String[] args) throws InterruptedException {

        String searchQuery = "example";
        String baseUrl = "https://nitter.net/search?f=tweets&q=";
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        int count = 0;
        while (count<10) {
            try {
                String searchUrl = baseUrl + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
                HtmlPage page = client.getPage(searchUrl);

                List<HtmlElement> items = page.getByXPath("//div[@class='timeline-item ']");
                if (items.isEmpty()) {
                    System.out.println("No items found !");
                } else {
                    for (HtmlElement htmlItem : items) {

                        HtmlElement spanBody = htmlItem.getFirstByXPath(".//div[@class='tweet-body']");

                        Item item = new Item();

                        item.setBody(spanBody.asNormalizedText());

                        ObjectMapper mapper = new ObjectMapper();
                        String jsonString = mapper.writeValueAsString(item);
                        //filter tweets that aren't retweets
                        Pattern pattern = Pattern.compile("(.*)retweet(.*)");
                        Matcher matcher = pattern.matcher(jsonString);
                        if (!(matcher.find())) {
                            insertTweet(jsonString);
                            System.out.println(jsonString);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("duplicate tweet");
            }
            count ++;
            if (count==10){System.exit(0);}
            Thread.sleep(10000);
        }
    }
        static MongoUtil mongoUtil = new MongoUtil();
        static MongoDatabase database = mongoUtil.getDB();
        static MongoCollection<Document> collection = database.getCollection("body");
        public static void insertTweet(String obj) throws InterruptedException
    {
        Document document = new Document("body", obj);
        collection.insertOne(document);
    }
}