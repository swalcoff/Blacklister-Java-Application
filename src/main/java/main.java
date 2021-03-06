import com.google.api.core.ApiFuture;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.paging.Page;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.common.collect.Lists;
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
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {

    public static boolean detectText(String filePath, blacklist black) throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        main m = new main();
        m.setP("/jack-project-e1e305e8b623.json");

        Credentials myCredentials = ServiceAccountCredentials.fromStream(
                m.getP());

        ImageAnnotatorSettings imageAnnotatorSettings =
                ImageAnnotatorSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
                        .build();

        try (ImageAnnotatorClient client =
                     ImageAnnotatorClient.create(imageAnnotatorSettings)) {
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
    public static void addToFire(blacklist bl, String username) {
        Firestore db = FirestoreClient.getFirestore();
        db.collection("blacklists").document(username).set(bl, SetOptions.merge());
    }

    public static void initFire(blacklist bl, String username) throws Exception{
        Map<String, Object> docData = new HashMap<>();
        docData.put("enabled", true);
        docData.put("list", bl.getList());
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> future = db.collection("blacklists").document(username).set(docData);
        System.out.println("Update time : " + future.get().getUpdateTime());
    }

    public static ArrayList getFire(String username) throws Exception{
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> ds= db.collection("blacklists").document(username).get();
        ArrayList<String> fireList = (ArrayList<String>) ds.get().get("list");
        return fireList;
    }

    public static boolean getBoolFire(String username) throws Exception{
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> ds= db.collection("blacklists").document(username).get();
        Boolean fBool = (Boolean) ds.get().get("enabled");
        return fBool;
    }

    public static void syncLists(blacklist bl, String username) throws Exception{
        List<String> temp = getFire(username);
        if(temp == null || temp.equals(bl.getList())){
            return;
        }
        bl.setList(getFire(username));
    }

    public static void checker(blacklist bl, String username) throws Exception{
        while(getBoolFire(username) == false){
            Thread.sleep(1000);
        }
        boolean bool = false;
        syncLists(bl, username);
        while(bool == false){
            bool = detectText(screencap(), bl);
            Thread.sleep(5000);
        }
        new myGui(bl, username);
    }

     public static void authExplicit(InputStream jsonPath) throws IOException, java.net.URISyntaxException {
         // You can specify a credential file by providing a path to GoogleCredentials.
         // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
         GoogleCredentials credentials = GoogleCredentials.fromStream(jsonPath)
                 .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
         Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

//         System.out.println("Buckets:");
         Page<Bucket> buckets = storage.list();
         for (Bucket bucket : buckets.iterateAll()) {
//             System.out.println(bucket.toString());
         }
     }

     InputStream getP(){
        return path;
     }



     void setP(String jsonPath) throws URISyntaxException, java.io.FileNotFoundException{
        path = this.getClass().getResourceAsStream(jsonPath);
     }

     InputStream path;

//     public static String username = "eric123";


    public static void main(String... args) throws Exception {
        String username = "scwalcof@ucsc.edu";

        main m = new main();
        m.setP("/jack-project-e1e305e8b623.json");
        System.out.println(m.getP().toString());

        authExplicit(m.getP());
        m.setP("/ericsproject2-25b4b-firebase-adminsdk-3pqxo-a9ce82cc7d.json");
        InputStream serviceAccount = m.getP();
        FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl("https://ericsproject2-25b4b.firebaseio.com").build();
        FirebaseApp.initializeApp(options);

        blacklist mainBlack = new blacklist();

        syncLists(mainBlack, username);
        initFire(mainBlack, username);



        if(mainBlack.getList().isEmpty()){
            new initGui(mainBlack, username);
        }else{
            checker(mainBlack, username);
        }



        System.out.println("Program finished");

    }
}