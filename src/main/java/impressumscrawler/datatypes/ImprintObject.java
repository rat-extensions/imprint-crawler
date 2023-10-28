package impressumscrawler.datatypes;

import java.util.ArrayList;

public class ImprintObject {

    private String docTitle;

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public boolean isHas_ust_numbers() {
        return has_ust_numbers;
    }

    public String getUst_number() {
        return ust_number;
    }

    public String report() {
        String report = "";
        report += "Doc title: " + docTitle + "\n";
        report += "Hauptadresse: \n";
        report += this.adress ;
        if(address_contains_firm) {
            report += "\n ist Adresse einer FIRMA \n";
            report += "\nEs sind folgende Vertreter gelistet: \n";
            report += vertreter + "\n";
        } else if (address_contains_name) {
            report += "\n ist Adresse einer PERSON \n";
        } else {
            report += "\n Adresse gibt keine Angabe ob FIRMA oder NAME \n";
            report += vertreter + "\n";
        }

        if(has_ust_numbers) {
            report += "\nEs wurden folgende ust Nummer gefunden: \n";
            report += ust_number + "\n";
        } else {
            report += "\nEs wurde keine ust-nummer gefunden werden \n";
        }

        if(address_contains_firm) {
            report += "Rechtsform der Firma: " + rechtsform + "\n";
        } else {
            report += "Es liegt keine Rechtsform vor. \n";
        }

        if(has_elektronische_kontaktaufnahme) {
            report += "\nFormular für elektronischen Kontaktaufnahme: \n";
            for (String cf : contactforms) {
                report += cf + "\n";
            }
        } else {
            report += "\nEs konnte kein elektronisches Kontaktformular gefunden werden \n";
        }

        if(has_email_address) {
            report += "\nGefundene Mailadressen: \n";
            for (String m : mails) {
                report += m + "\n";
            }
        } else {
            report += "\nEs konnte keine Mails gefunden werden \n";
        }

        if(numericContactObjects.size() > 0) {
            report += "\nGefundene Nummern: \n";
            for (NumericContactObject nco : numericContactObjects) {
                String contact = nco.getContact_type() + " = " + nco.getContact_info();
                report += contact + "\n";
            }
        } else {
            report += "\nEs konnten keine Nummern gefunden werden \n";
        }

        if (has_register_info_indicator) {
            report += "\nGefundene Registernummern: \n";
            for (RegisterObject ro : registerObjects){
                report += ro.report() + "\n";
            }
        } else {
            report += "\nEs konnten keine Registernummern oder Registergerichte gefunden werden\n";
        }

        if(contains_aufsichtsbehoerde_indicator) {
            report += "\nAufsichtsbehoerde: " + aufsichtsbehoerde + "\n";
        } else {
            report += "\nkeine Angabe über eine Aufsichtsbehoerde gefunden \n";
        }

        if (has_chambererd_job_indicator) {
            report += "\nEs wird ein verkammerter Beruf genannt: \n";
            for (String bb : berufsbezeichnung) {
                report += bb + ", ";
            }
            report += "\n";
        } else {
            report += "\nEs wird KEIN verkammerter Beruf genannt \n";
        }

        if(has_chamber_indicator) {
            report += "\nFolgende Kammern wurden gefunden: \n";
            for (String k : chambers) {
                report += k + "\n";
            }
            report += "\nEs werden folgende Berufsrechtlichen regelungen genannt: \n";
            report += berufsrechliche_regelung;
        } else {
            report += "\nEs liegt KEIN Indikator für eine Kammer vor und somit auch keine Kammern oder berufsrechliche Regelungen. \n";
        }

        report += "\n---- adressObjects: \n";
        for (AdressObject ao : adressObjects) {
            report += ao.report();
        }

        report += "\n---- vertretungsberechtigterObjects: \n";
        for(VertretungsberechtigterObject vo : vertretungsberechtigterObjects){
            report += vo.report();
        }
        report += "\n---- mails: \n";
        for (String m : mails) {
            report += m + "\n";
        }
        report += "\n---- contactforms: \n";
        for (String cf : contactforms) {
            report += cf + "\n";
        }
        report += "\n---- NumericContactobjects: \n";
        for (NumericContactObject co : numericContactObjects) {
            report += co.report();
        }
        report += "\n---- aufsichtsObject: \n";
        report += aufsichtsObject.report();

        report += "\n---- redaktionsObjects: \n";
        for (RedaktionsObject ro : redaktionsObjects) {
            report += ro.report();
        }
        report += "\n---- editorInfoObjects: \n";
        for (EditorInfoObject eio : editorInfoObjects) {
            report += eio.report();
        }
        report += "\n---- ust_IdentificationNumbers: \n";
        for (Ust_IdentificationNumber un : ust_identificationNumbers) {
            report += un.report();
        }
        report += "\n---- chamberObjects: \n";
        for (ChamberObject co : chamberObjects) {
            report += co.report();
        }
        report += "\n---- berufsordnungsObject: \n";
        report += berufsordnungsObject.report();

        report += "\n---- registerObjects: \n";
        for (RegisterObject ro : registerObjects) {
            report += ro.report();
        }

        report += "\n---- rechtsformObjects: \n";
        for (RechtsformObject ro : rechtsformObjects) {
            report += ro.report();
        }
        return report;
    }

    //nicht geschäftmäßige Nutzung

    private String adress; //set | line 130..

    private boolean address_contains_firm;
    private boolean has_vertreter;
    private String vertreter; //set | 130..

    private AdressObject main_address; //set | 130..

    private boolean address_contains_name; //set | 95

    private boolean multiple_addresses_no_main_address; //set

    //geschäftmäßige Nutzung
    //if firm
    private String rechtsform; //set
    private boolean has_elektronische_kontaktaufnahme; //set
    private String[] elektronische_kontaktaufnahme; //No need for this one.

    private boolean has_email_address; //set
    private boolean has_contact_form; //set

    private boolean contains_aufsichtsbehoerde_indicator; //set
    private String aufsichtsbehoerde; //set

    private boolean has_register_info_indicator;
    private String register_info;

    private boolean has_chambererd_job_indicator; //set
    private boolean has_chamber_indicator; //set
    private String[] chambers; //set

    private String[] berufsbezeichnung; //set
    private String berufsrechliche_regelung; //set

    private boolean has_ust_numbers; // set
    private String ust_number; //set

    //private boolean has_info_about_abwicklung
    //private String abwicklung_info;

    //private boolean is_audiovisuell_mediendienstanbieter;
    //private String mitgliedstaat;
    //private String aufsichtsbehoerde;


    //Redaktionaler Inhalt:
    private boolean has_editorial_indicator; //
    private String editors; //

    //All results -- raw

    ArrayList<AdressObject> adressObjects;
    ArrayList<VertretungsberechtigterObject> vertretungsberechtigterObjects;

    ArrayList<String> mails;
    ArrayList<String> contactforms;
    ArrayList<NumericContactObject> numericContactObjects;

    AufsichtsObject aufsichtsObject;

    ArrayList<RedaktionsObject> redaktionsObjects;
    ArrayList<EditorInfoObject> editorInfoObjects;

    ArrayList<Ust_IdentificationNumber> ust_identificationNumbers;

    ArrayList<ChamberObject> chamberObjects;
    BerufsordnungsObject berufsordnungsObject;

    ArrayList<RegisterObject> registerObjects;

    ArrayList<RechtsformObject> rechtsformObjects;

    // getter and setter

    public boolean isHas_contact_form() {
        return has_contact_form;
    }

    public void setHas_contact_form(boolean has_contact_form) {
        this.has_contact_form = has_contact_form;
    }

    public boolean isHas_email_address() {
        return has_email_address;
    }

    public void setHas_email_address(boolean has_email_address) {
        this.has_email_address = has_email_address;
    }

    public boolean isHas_elektronische_kontaktaufnahme() {
        return has_elektronische_kontaktaufnahme;
    }

    public void setHas_elektronische_kontaktaufnahme(boolean has_elektronische_kontaktaufnahme) {
        this.has_elektronische_kontaktaufnahme = has_elektronische_kontaktaufnahme;
    }

    public ArrayList<NumericContactObject> getNumericContactObjects() {
        return numericContactObjects;
    }

    public void setNumericContactObjects(ArrayList<NumericContactObject> numericContactObjects) {
        this.numericContactObjects = numericContactObjects;
    }

    public ArrayList<RechtsformObject> getRechtsformObjects() {
        return rechtsformObjects;
    }

    public void setRechtsformObjects(ArrayList<RechtsformObject> rechtsformObjects) {
        this.rechtsformObjects = rechtsformObjects;
    }

    public boolean isMultiple_addresses_no_main_address() {
        return multiple_addresses_no_main_address;
    }

    public void setMultiple_addresses_no_main_address(boolean multiple_addresses_no_main_address) {
        this.multiple_addresses_no_main_address = multiple_addresses_no_main_address;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getVertreter() {
        return vertreter;
    }

    public void setVertreter(String vertreter) {
        this.vertreter = vertreter;
    }

    public boolean isHas_vertreter() {
        return has_vertreter;
    }

    public void setHas_vertreter(boolean has_vertreter) {
        this.has_vertreter = has_vertreter;
    }

    public AdressObject getMain_address() {
        return main_address;
    }

    public void setMain_address(AdressObject main_address) {
        this.main_address = main_address;
    }

    public boolean isAddress_contains_name() {
        return address_contains_name;
    }

    public void setAddress_contains_name(boolean address_contains_name) {
        this.address_contains_name = address_contains_name;
    }

    public boolean isAddress_contains_firm() {
        return address_contains_firm;
    }

    public void setAddress_contains_firm(boolean address_contains_firm) {
        this.address_contains_firm = address_contains_firm;
    }

    public String getRechtsform() {
        return rechtsform;
    }

    public void setRechtsform(String rechtsform) {
        this.rechtsform = rechtsform;
    }

    public String[] getElektronische_kontaktaufnahme() {
        return elektronische_kontaktaufnahme;
    }

    public void setElektronische_kontaktaufnahme(String[] elektronische_kontaktaufnahme) {
        this.elektronische_kontaktaufnahme = elektronische_kontaktaufnahme;
    }

    public boolean isContains_aufsichtsbehoerde_indicator() {
        return contains_aufsichtsbehoerde_indicator;
    }

    public void setContains_aufsichtsbehoerde_indicator(boolean contains_aufsichtsbehoerde_indicator) {
        this.contains_aufsichtsbehoerde_indicator = contains_aufsichtsbehoerde_indicator;
    }

    public String getAufsichtsbehoerde() {
        return aufsichtsbehoerde;
    }

    public void setAufsichtsbehoerde(String aufsichtsbehoerde) {
        this.aufsichtsbehoerde = aufsichtsbehoerde;
    }

    public boolean isHas_register_info_indicator() {
        return has_register_info_indicator;
    }

    public void setHas_register_info_indicator(boolean has_register_info_indicator) {
        this.has_register_info_indicator = has_register_info_indicator;
    }

    public String getRegister_info() {
        return register_info;
    }

    public void setRegister_info(String register_info) {
        this.register_info = register_info;
    }

    public boolean isHas_chambererd_job_indicator() {
        return has_chambererd_job_indicator;
    }

    public void setHas_chambererd_job_indicator(boolean has_chambererd_job_indicator) {
        this.has_chambererd_job_indicator = has_chambererd_job_indicator;
    }

    public boolean isHas_chamber_indicator() {
        return has_chamber_indicator;
    }

    public void setHas_chamber_indicator(boolean has_chamber_indicator) {
        this.has_chamber_indicator = has_chamber_indicator;
    }

    public String[] getChambers() {
        return chambers;
    }

    public void setChambers(String[] chambers) {
        this.chambers = chambers;
    }

    public String[] getBerufsbezeichnung() {
        return berufsbezeichnung;
    }

    public void setBerufsbezeichnung(String[] berufsbezeichnung) {
        this.berufsbezeichnung = berufsbezeichnung;
    }

    public String getBerufsrechliche_regelung() {
        return berufsrechliche_regelung;
    }

    public void setBerufsrechliche_regelung(String berufsrechliche_regelung) {
        this.berufsrechliche_regelung = berufsrechliche_regelung;
    }

    public boolean ishas_ust_numbers() {
        return has_ust_numbers;
    }

    public void setHas_ust_numbers(boolean has_ust_numbers) {
        this.has_ust_numbers = has_ust_numbers;
    }

    public String getUst_numbers() {
        return ust_number;
    }

    public void setUst_number(String ust_number) {
        this.ust_number = ust_number;
    }

    public boolean isHas_editorial_indicator() {
        return has_editorial_indicator;
    }

    public void setHas_editorial_indicator(boolean has_editorial_indicator) {
        this.has_editorial_indicator = has_editorial_indicator;
    }

    public String getEditors() {
        return editors;
    }

    public void setEditors(String editors) {
        this.editors = editors;
    }

    public ArrayList<AdressObject> getAdressObjects() {
        return adressObjects;
    }

    public void setAdressObjects(ArrayList<AdressObject> adressObjects) {
        this.adressObjects = adressObjects;
    }

    public ArrayList<VertretungsberechtigterObject> getVertretungsberechtigterObjects() {
        return vertretungsberechtigterObjects;
    }

    public void setVertretungsberechtigterObjects(ArrayList<VertretungsberechtigterObject> vertretungsberechtigterObjects) {
        this.vertretungsberechtigterObjects = vertretungsberechtigterObjects;
    }

    public ArrayList<String> getMails() {
        return mails;
    }

    public void setMails(ArrayList<String> mails) {
        this.mails = mails;
    }

    public ArrayList<String> getContactforms() {
        return contactforms;
    }

    public void setContactforms(ArrayList<String> contactforms) {
        this.contactforms = contactforms;
    }

    public AufsichtsObject getAufsichtsObject() {
        return aufsichtsObject;
    }

    public void setAufsichtsObject(AufsichtsObject aufsichtsObject) {
        this.aufsichtsObject = aufsichtsObject;
    }

    public ArrayList<RedaktionsObject> getRedaktionsObjects() {
        return redaktionsObjects;
    }

    public void setRedaktionsObjects(ArrayList<RedaktionsObject> redaktionsObjects) {
        this.redaktionsObjects = redaktionsObjects;
    }

    public ArrayList<EditorInfoObject> getEditorInfoObjects() {
        return editorInfoObjects;
    }

    public void setEditorInfoObjects(ArrayList<EditorInfoObject> editorInfoObjects) {
        this.editorInfoObjects = editorInfoObjects;
    }

    public ArrayList<Ust_IdentificationNumber> getUst_identificationNumbers() {
        return ust_identificationNumbers;
    }

    public void setUst_identificationNumbers(ArrayList<Ust_IdentificationNumber> ust_identificationNumbers) {
        this.ust_identificationNumbers = ust_identificationNumbers;
    }

    public ArrayList<ChamberObject> getChamberObjects() {
        return chamberObjects;
    }

    public void setChamberObjects(ArrayList<ChamberObject> chamberObjects) {
        this.chamberObjects = chamberObjects;
    }

    public BerufsordnungsObject getBerufsordnungsObject() {
        return berufsordnungsObject;
    }

    public void setBerufsordnungsObject(BerufsordnungsObject berufsordnungsObject) {
        this.berufsordnungsObject = berufsordnungsObject;
    }

    public ArrayList<RegisterObject> getRegisterObjects() {
        return registerObjects;
    }

    public void setRegisterObjects(ArrayList<RegisterObject> registerObjects) {
        this.registerObjects = registerObjects;
    }
}
