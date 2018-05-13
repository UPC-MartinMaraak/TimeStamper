package miasoft.timestamper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

class TimeStamp {

    private Element xmlElement;

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
        String in = getCheckIn() != null ? getCheckIn().toString() : "null";
        String out = getCheckOut() != null ?getCheckOut().toString() : "null";

        return in +" -> " + out +" | " + getDescription();
    }
}
