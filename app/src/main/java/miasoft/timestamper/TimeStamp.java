package miasoft.timestamper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;

class TimeStamp {

    private Element xmlElement;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YY hh:mm");

    public TimeStamp(Document document, Date checkIn, Date checkOut, String description) {
        xmlElement = document.createElement("TimeStamp");
        setCheckIn(checkIn);
        setCheckOut(checkOut);
        setDescription(description);
    }

    public TimeStamp(Element element) {
        this.xmlElement = element;
    }

    public Date getCheckIn() {
        if(!xmlElement.getAttribute("checkIn").equals("")) {
            return new Date(Long.parseLong(xmlElement.getAttribute("checkIn")));
        }else{
            return null;
        }}

    public void setCheckIn(Date checkIn) {
        xmlElement.setAttribute("checkIn",String.valueOf(checkIn.getTime()));
    }

    public Date getCheckOut() {
        if(!xmlElement.getAttribute("checkOut").equals("")) {
            return new Date(Long.parseLong(xmlElement.getAttribute("checkOut")));
        }else{
            return null;
        }
    }

    public void setCheckOut(Date checkOut) {
        if(checkOut != null) {
            xmlElement.setAttribute("checkOut", String.valueOf(checkOut.getTime()));
        }else{
            xmlElement.setAttribute("checkOut", "");
        }
    }

    public String getDescription() {
        return xmlElement.getAttribute("description");
    }

    public void setDescription(String description) {
        xmlElement.setAttribute("description",description);
    }

    public Element getXmlElement() {
        return xmlElement;
    }

    @Override
    public String toString() {
        String in = getCheckIn() != null ? sdf.format(getCheckIn()) : "not registered";
        String out = getCheckOut() != null ? sdf.format(getCheckOut()) : "not registered";

        return in +" -> " + out +" | " + getDescription();
    }
}
