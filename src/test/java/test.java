import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

public class test {
    @Test
    public void test(){
        String t = "系列，一个是 Special Victims Unit 系列（6季），一个是Criminal Intent 系列（4季），和Trial By Jury 系列。可见其火爆程度。堪称NBC历史上，乃至美国影史上最宏伟的剧集。 \"In the criminal xfplay5.com justice system, the people are represented by two separate yet equally important groups: the police, who investigate crime; and the district attorneys, who prosecute the offenders. These are there stories. \" 这是Law & Order十四年如一日，也是我百听不厌的开场白，是以Law & Order franchise(包括s";
        t.replaceAll("\"([^\"]*)\"", "$1");
        JSONObject object = JSON.parseObject(t);
        System.out.println("==============");

    }
}
