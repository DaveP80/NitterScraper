import java.net.URLEncoder;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebScraper {

    public static void main(String[] args) {

        String searchQuery = "example" ;
        String baseUrl = "https://nitter.net/search?f=tweets&q=" ;
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
                    String jsonString = mapper.writeValueAsString(item) ;

                    System.out.println(jsonString);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}