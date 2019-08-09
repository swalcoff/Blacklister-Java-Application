import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.protobuf.ByteString;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuickstartSample {

    public static boolean detectText(String filePath, blacklist black) throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return false;
                }
                Object[] bArray = black.getList().toArray();
                //For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    for(int i = 0; i < bArray.length; i++ ){
                        if(annotation.getDescription().toLowerCase().indexOf(black.get(i)) != -1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String screencap(){
        String format = "jpg";
        String fileName = "FullScreenshot." + format;
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenFullImage, format, new File(fileName));

            System.out.println("A full screenshot saved!");
        } catch (AWTException | IOException ex) {
            System.err.println(ex);
        }
        return fileName;
    }

    public static void closeScreen(){
        String[] mac = new String[]{"/bin/bash", "-c", "/System/Library/CoreServices/Menu\\ Extras/user.menu/Contents/Resources/CGSession -suspend", "", ""};
        try {
            new ProcessBuilder(mac).start();
        }catch (IOException e1){
            e1.printStackTrace();
        }
    }

    public static void closeBrowser(){
        String[] mac = new String[]{"/bin/bash", "-c", "pkill Google Chrome", "", ""};
        try {
            new ProcessBuilder(mac).start();
        }catch (IOException e1){}
    }

//    public static void addToFire(String url, blacklist bl) throws Exception{
//        bl.add(url);
//        Firestore db = FirestoreClient.getFirestore();
//        ApiFuture<WriteResult> future = db.collection("blacklist").document(username).set(bl);
//        ApiFuture<DocumentSnapshot> ds= db.collection("blacklist").document("black1").get();
//        System.out.println("LIST: " + ds.get().get("list"));
//    }
    public static void addToFire(blacklist bl) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("blacklist").document(username).set(bl, SetOptions.merge());
    }

    public static ArrayList getFire() throws Exception{
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> ds= db.collection("blacklist").document(username).get();
        ArrayList<String> fireList = (ArrayList<String>) ds.get().get("list");
        return fireList;
    }

    public static boolean getBoolFire() throws Exception{
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> ds= db.collection("blacklist").document(username).get();
        Boolean fBool = (Boolean) ds.get().get("running");
        return fBool;
    }

    public static void syncLists(blacklist bl) throws Exception{
        if(getFire().equals(bl.getList())){
            return;
        }
        bl.setList(getFire());
    }

    public static void checker(blacklist bl) throws Exception{
        if (getBoolFire() == false) return;
        boolean bool = false;
        syncLists(bl);
        while(bool == false){
            bool = detectText(screencap(), bl);
            Thread.sleep(5000);
        }
        new myGui(bl);
    }



    public static String username = "Eric123";


    public static void main(String... args) throws Exception {
        FileInputStream serviceAccount = new FileInputStream("/Users/Samuel/Desktop/IntelliJ projects/googleVisionTest/ericsproject2-25b4b-firebase-adminsdk-3pqxo-a9ce82cc7d.json");
        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl("https://ericsproject2-25b4b.firebaseio.com").build();
        FirebaseApp.initializeApp(options);

        blacklist mainBlack = new blacklist();

        System.out.println(mainBlack);


        checker(mainBlack);

        System.out.println("Program finished");

    }
}