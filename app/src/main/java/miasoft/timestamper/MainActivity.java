package miasoft.timestamper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {

    private String fileName = "timeLog.xml";
    private Document xmlDocument;
    private Button btnCheckIn;
    private Button btnCheckOut;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheckIn = findViewById(R.id.btnCheckIn);
        btnCheckOut = findViewById(R.id.btnEndDay);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        boolean ok = false;
        try {
            initializeFile();
            ok = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDay();
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDay();
            }
        });

        try {
            updateListView();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateListView() {
        List<String> listViewValues = new ArrayList<String>();

        for (TimeStamp timeStamp : getTimeStamps(xmlDocument)) {

            listViewValues.add(timeStamp.toString());
        }
        ListView timeListView = (ListView) findViewById(R.id.lstvTimeList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                listViewValues.toArray(new String[listViewValues.size()]));

        timeListView.setAdapter(adapter);



    }

    private List<TimeStamp> getTimeStamps(Document xmlDocument) {

        List<TimeStamp> result = new ArrayList<TimeStamp>();
        NodeList nodes = xmlDocument.getDocumentElement().getChildNodes();
        for(int i =0;i<nodes.getLength();i++ ){
            Node node = nodes.item(i);
            Element element = (Element) node;
            result.add(new TimeStamp(element));

        }

        return result;
    }


    private void startDay(){
        xmlDocument.getDocumentElement().appendChild(new TimeStamp(xmlDocument, Calendar.getInstance().getTime(),null,"").getXmlElement());
        writeToDocument();
    }


    private void endDay(){
        final Date localTime = Calendar.getInstance().getTime();
        TimeStamp timeStamp = null;
        for(TimeStamp tmp : getTimeStamps(xmlDocument)){
            if(tmp.getCheckIn().getYear() == localTime.getYear() && tmp.getCheckIn().getDay() == localTime.getDay()){
                timeStamp = tmp;
                break;
            }

        }
        if(timeStamp != null){
            timeStamp.setCheckOut(localTime);
        }else{
            xmlDocument.getDocumentElement().appendChild(new TimeStamp(xmlDocument,Calendar.getInstance().getTime(),Calendar.getInstance().getTime(),"").getXmlElement());

        }
        writeToDocument();
    }

    private void writeToDocument() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            Transformer transformer  = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(xmlDocument);
            StreamResult result = new StreamResult(new File(filePath));

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            System.out.println(e.getMessageAndLocation());
        }


        System.out.println("File saved!");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        updateListView();
    }

    private void initializeFile() throws IOException, ParserConfigurationException, SAXException {
        filePath = Environment.getExternalStorageDirectory() + File.separator + fileName;
        File  file = new File(filePath);
        if(!file.exists()) {
            file.createNewFile();
            xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            xmlDocument.appendChild(xmlDocument.createElement("TimeStamps"));
            writeToDocument();
        }else{
            try {
                xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            }catch (SAXParseException e){
                System.out.println("Error, could not read xml document");
                xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                xmlDocument.appendChild(xmlDocument.createElement("TimeStamps"));
                writeToDocument();
            }

        }

    }


}
