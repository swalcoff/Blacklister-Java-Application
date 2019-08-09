import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class blacklist extends QuickstartSample {
    private List<String> list = new ArrayList<String>();

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void add(String s) throws Exception{
        if(list.contains(s) == false){
            list.add(s);
        }
    }

    public String get(int i){
        return this.list.get(i);
    }

    public boolean isElem(String s){
        return list.contains(s);
    }

//    public int length(){
//        return list.size();
//    }

//    public String elem(int i){
//        return list[i];
//    }
//

    public String toString(){
        Iterator itr = list.iterator();
        String s1 = "";
        while (itr.hasNext())
        {
            //  moving cursor to next element
            String i = (String)itr.next();
            s1 += i + ", ";

        }
        String s2 = "";
        for (int i = 0; i < s1.length() - 2; i++ ){
            s2 += s1.charAt(i);
        }
        return s2;
    }


}
