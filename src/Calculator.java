import java.io.*;
import java.sql.Ref;
import java.sql.Timestamp;
import java.util.*;

public class Calculator {
    static ArrayList<Participant> participants = new ArrayList<>();
    static HashMap<Integer,Double> Table_2_AvgError = new HashMap<>();     // Table2's data
    static HashMap<String,Integer> Table_1_AvgError = new HashMap<>();     // Table1's data
    static HashMap<String,HashMap<String,Double>> Graph_1_AvgError_Heading = new HashMap<>();  // Average error graph for heading.
    static ArrayList<HashMap<String,Double>> Graph_2_AvgError_Target = new ArrayList<>();   // Average error graph for target.

    static ArrayList<HashMap<String,Double>> Graph_2_AvgError_Target_sp = new ArrayList<>();   // Average error graph for target.

    static HashMap<Integer,Double> Table_2_Time = new HashMap<>();     // Extra Table2's data
    static HashMap<String,Integer> Table_1_Time = new HashMap<>();     // Extra Table1's data
    static HashMap<String,HashMap<String,Double>> Graph_1_Time_Heading = new HashMap<>();  // Average error graph for heading.
    static ArrayList<HashMap<String,Double>> Graph_2_Time_Target = new ArrayList<>();   // Average error graph for target.

    static String filename = "bfix.csv";  // type in output file's name

    public static void main(String[] args){

        InitialMethod();

        Calculate();
        Generator();

        OutputGenerator();

        Reference_Generator();
        Generate_Graph_2_SP();
        Output_G2_SP();

    }

    public static void InitialMethod(){
        BufferedReader br = null;
        FileReader fr = null;

        try {
            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            int sID = 0;
            int pID = 1;  // the participant's ID start with 1 and also count the number of participants.

            Participant participant = null;
            HashSet<String> angleChecking = new HashSet<>();    // to check whether any angle pair exist or not.

            String sCurrentLine;
            String firstLine = br.readLine();   // eliminate the first line.

            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println(sCurrentLine);
                String[] parts = sCurrentLine.split(",");
                String session_ID = parts[0];
                String obj_pos = parts[1];
                String heading = parts[2];
                String target = parts[3];
                String value = parts[4];
                String time = parts[5];
                String viewpoint = parts[6];

                if (sID != Integer.parseInt(session_ID)){       // if the session's ID changed, then this is a new participant.
                    if (sID!=0){
                        participants.add(participant);  // if a new participant come, then add the old one in the participants.
                    }
                    sID = Integer.parseInt(session_ID);
                    participant = new Participant(pID, sID, viewpoint);     // create a new participant object.
                    pID++;
                    angleChecking.clear();  // clear when it is a new participant.
                }

                String HT_angle_Str = heading.concat(":"+target);
                if (!angleChecking.contains(HT_angle_Str) && participant != null){    // if there is no PointingError of any HT_angle_Str, then create a new one.
                    ArrayList<Double> error = new ArrayList<>();
                    ArrayList<Double> timeS = new ArrayList<>();

                    participant.PointingError.put(HT_angle_Str, error);
                    participant.Spent_Time.put(HT_angle_Str,timeS);

                    angleChecking.add(HT_angle_Str);    // add HY_angle_Str into angleChecking set in order to used it to check the existence of the heading and target in PointingError.
                }
                if (participant != null) {


                    double answer = 360.0 - Double.parseDouble(heading) + Double.parseDouble(target);
                    if (answer >= 360){
                        if (answer > 360) answer = answer - 360;
                        else answer = 0;
                    }

                    double pointingError = Double.MAX_VALUE;
                    double F_option = Math.abs(answer - Double.parseDouble(value));
                    double S_option = 360 - Double.parseDouble(value) + answer;
                    double T_option = 360 - answer + Double.parseDouble(value);
                    ArrayList<Double> options = new ArrayList<>();
                    options.add(F_option);
                    options.add(S_option);
                    options.add(T_option);
                    for (Double n : options){
                        if (pointingError > n) pointingError = n;
                    }
                    participant.PointingError.get(HT_angle_Str).add(pointingError); // put the error into specific HT_angle_Str.
                    //System.out.println(HT_angle_Str+" : "+pointingError);
                    //time = "4.30555";
                    participant.Spent_Time.get(HT_angle_Str).add(Double.parseDouble(time));
                }
            }

            /*This is for the last participant*/
            participants.add(participant);  // add the last participant because while loop which contains the adding participant part will terminate before adding the last one.

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Generator(){

        Generate_Table1();
        Generate_Table2();
        Generate_Graph_1();
        Generate_Graph_2();

        Generate_Table1_Extra();
        Generate_Table2_Extra();
        Generate_Graph_1_Extra();
        Generate_Graph_2_Extra();

    }

    public static void Calculate(){
        for (Participant participant : participants){
            participant.Calculate_AvgPointingError();       // to calculate the AvgPointingError.
            participant.Calculate_AvgPointingError_Each_Heading();
            participant.Calculate_AvgPointingError_Each_Target();
            participant.Calculate_StdDev_EachHeading();
            participant.Calculate_ReferenceDirection();
            participant.Calculate_ErrorPointing_Judgment();

            participant.Calculate_AvgSpent_Time();
            participant.Calculate_AvgSpentTime_Each_Heading();
            participant.Calculate_AvgSpentTime_Each_Target();
            participant.Calculate_StdDev_Time_EachHeading();
            participant.Calculate_ReferenceDirection_byTime();
            participant.Calculate_Time_Judgment();
        }
    }

    public static void  Generate_Table1(){

        Table_1_AvgError.put("0",0);
        Table_1_AvgError.put("45",0);
        Table_1_AvgError.put("90",0);
        Table_1_AvgError.put("135",0);
        Table_1_AvgError.put("180",0);
        Table_1_AvgError.put("225",0);
        Table_1_AvgError.put("270",0);
        Table_1_AvgError.put("315",0);

        for (Participant participant : participants){
            //System.out.println(participant.ReferenceDirection);
            for (String ref : participant.ReferenceDirection){
                Table_1_AvgError.put(ref,Table_1_AvgError.get(ref) + 1);
            }
        }
        //System.out.println(Table_1_AvgError);
    }

    public static void  Generate_Table1_Extra(){

        Table_1_Time.put("0",0);
        Table_1_Time.put("45",0);
        Table_1_Time.put("90",0);
        Table_1_Time.put("135",0);
        Table_1_Time.put("180",0);
        Table_1_Time.put("225",0);
        Table_1_Time.put("270",0);
        Table_1_Time.put("315",0);

        for (Participant participant : participants){
            //System.out.println(participant.ReferenceDirection_byTime);
            for (String ref : participant.ReferenceDirection_byTime){
                Table_1_Time.put(ref,Table_1_Time.get(ref) + 1);
            }
        }
        //System.out.println(Table_1_Time);
    }

    public static void Generate_Table2(){
        ArrayList<Double> sumError = new ArrayList<>();
        ArrayList<Integer> Gsize = new ArrayList<>();
        ArrayList<Double> Value =  new ArrayList<>();
        for (int i=0; i<4; i++){
            sumError.add(0.0);
            Gsize.add(0);
        }
        for (Participant participant : participants){
            for (String key : participant.ErrorPointing_Judgment.get(0).keySet()){  // For the 1st group.
                sumError.set(0,sumError.get(0) + participant.ErrorPointing_Judgment.get(0).get(key));
            }
            for (String key : participant.ErrorPointing_Judgment.get(1).keySet()){  // For the 2nd group.
                sumError.set(1,sumError.get(1) + participant.ErrorPointing_Judgment.get(1).get(key));
            }
            for (String key : participant.ErrorPointing_Judgment.get(2).keySet()){  // For the 3rd group.
                sumError.set(2,sumError.get(2) + participant.ErrorPointing_Judgment.get(2).get(key));
            }
            for (String key : participant.ErrorPointing_Judgment.get(3).keySet()){  // For the 4th group.
                sumError.set(3,sumError.get(3) + participant.ErrorPointing_Judgment.get(3).get(key));
            }
            for (int i=0; i<4; i++){
                Gsize.set(i,Gsize.get(i) +  participant.ErrorPointing_Judgment.get(i).size());
            }
        }
        for (int i=0; i<4; i++){
            Value.add(i, sumError.get(i) / Gsize.get(i));
        }
        for (int i=1; i<=Value.size(); i++){
            Table_2_AvgError.put(i, Value.get(i-1));
        }
//        System.out.println(Value);
//        System.out.println(Table_2_AvgError);
    }

    public static void Generate_Table2_Extra(){
        ArrayList<Double> sumTime = new ArrayList<>();
        ArrayList<Integer> Gsize = new ArrayList<>();
        ArrayList<Double> Value =  new ArrayList<>();
        for (int i=0; i<4; i++){
            sumTime.add(0.0);
            Gsize.add(0);
        }
        for (Participant participant : participants){
            for (String key : participant.Time_Judgment.get(0).keySet()){  // For the 1st group.
                sumTime.set(0,sumTime.get(0) + participant.Time_Judgment.get(0).get(key));
            }
            for (String key : participant.Time_Judgment.get(1).keySet()){  // For the 2nd group.
                sumTime.set(1,sumTime.get(1) + participant.Time_Judgment.get(1).get(key));
            }
            for (String key : participant.Time_Judgment.get(2).keySet()){  // For the 3rd group.
                sumTime.set(2,sumTime.get(2) + participant.Time_Judgment.get(2).get(key));
            }
            for (String key : participant.Time_Judgment.get(3).keySet()){  // For the 4th group.
                sumTime.set(3,sumTime.get(3) + participant.Time_Judgment.get(3).get(key));
            }
            for (int i=0; i<4; i++){
                Gsize.set(i,Gsize.get(i) +  participant.Time_Judgment.get(i).size());
            }
        }
        for (int i=0; i<4; i++){
            double c = sumTime.get(i) / Gsize.get(i);
            if (Double.isNaN(c)){
                c = 0.0;
            }
            Value.add(i, c);
        }
        for (int i=1; i<=Value.size(); i++){
            Table_2_Time.put(i, Value.get(i-1));
        }

//        System.out.println(Value);
//        System.out.println(Table_2_Time);
    }

    public static void Generate_Graph_1(){

        ArrayList<ArrayList<Participant>> View_Point_Filter = new ArrayList<>();
        ArrayList<String> viewPoint = new ArrayList<>(); viewPoint.add("0"); viewPoint.add("180");
        for (int i=0; i<2; i++){
            ArrayList<Participant> p = new ArrayList<>();
            View_Point_Filter.add(p);
        }
        for (Participant participant : participants){
            if (participant.ViewPoint.equals("0")){
                View_Point_Filter.get(0).add(participant);       // 0 degree group is in index 0, 180 degree group is in index 1.
            }
            else View_Point_Filter.get(1).add(participant);
        }

        for (int i=0; i<View_Point_Filter.size(); i++){
            HashMap<String,Double> AvgError_Heading = new HashMap<>();
            for (Participant participant : View_Point_Filter.get(i)){
                for (String heading : participant.AvgPointingError_Each_Heading.keySet()){
                    if (!AvgError_Heading.containsKey(heading)) AvgError_Heading.put(heading, participant.AvgPointingError_Each_Heading.get(heading));
                    else AvgError_Heading.put(heading, AvgError_Heading.get(heading) + participant.AvgPointingError_Each_Heading.get(heading));
                }
            }
            for (String heading : AvgError_Heading.keySet()){
                AvgError_Heading.put(heading, AvgError_Heading.get(heading) /View_Point_Filter.get(i).size());
            }
            Graph_1_AvgError_Heading.put(viewPoint.get(i), AvgError_Heading);
        }
        //System.out.println(Graph_1_AvgError_Heading);

    }

    public static void Generate_Graph_1_Extra(){

        ArrayList<ArrayList<Participant>> View_Point_Filter = new ArrayList<>();
        ArrayList<String> viewPoint = new ArrayList<>(); viewPoint.add("0"); viewPoint.add("180");
        for (int i=0; i<2; i++){
            ArrayList<Participant> p = new ArrayList<>();
            View_Point_Filter.add(p);
        }
        for (Participant participant : participants){
            if (participant.ViewPoint.equals("0")){
                View_Point_Filter.get(0).add(participant);       // 0 degree group is in index 0, 180 degree group is in index 1.
            }
            else View_Point_Filter.get(1).add(participant);
        }

        for (int i=0; i<View_Point_Filter.size(); i++){
            HashMap<String,Double> AvgError_Heading = new HashMap<>();
            for (Participant participant : View_Point_Filter.get(i)){
                for (String heading : participant.AvgSpentTime_Each_Heading.keySet()){
                    if (!AvgError_Heading.containsKey(heading)) AvgError_Heading.put(heading, participant.AvgSpentTime_Each_Heading.get(heading));
                    else AvgError_Heading.put(heading, AvgError_Heading.get(heading) + participant.AvgSpentTime_Each_Heading.get(heading));
                }
            }
            for (String heading : AvgError_Heading.keySet()){
                AvgError_Heading.put(heading, AvgError_Heading.get(heading) /View_Point_Filter.get(i).size());
            }
            Graph_1_Time_Heading.put(viewPoint.get(i), AvgError_Heading);
        }
        //System.out.println(Graph_1_Time_Heading);

    }

    public static void Generate_Graph_2(){

        ArrayList<ArrayList<Integer>> T = new ArrayList<>();
        ArrayList<Integer> _T1 = new ArrayList<>();
        _T1.add(0);_T1.add(90);_T1.add(180);_T1.add(270);
        T.add(_T1);
        ArrayList<Integer> _T2 = new ArrayList<>();
        _T2.add(45);_T2.add(135);_T2.add(225);_T2.add(315);
        T.add(_T2);

        for (int i=0; i<2; i++) {
            ArrayList<HashMap<String,Double>> c = new ArrayList<>();
            for (Participant participant : participants) {
                HashMap<String, Double> tag = new HashMap<>();
                for (String HT : participant.AvgPointingError.keySet()) {
                    String[] parts = HT.split(":");
                    if (T.get(i).contains(Integer.parseInt(parts[0]))) {
                        if (!tag.containsKey(parts[1])) tag.put(parts[1], participant.AvgPointingError.get(HT));
                        else tag.put(parts[1], tag.get(parts[1]) + participant.AvgPointingError.get(HT));
                    }
                }
                for (String key : tag.keySet()) {
                    tag.put(key, tag.get(key) / 4);
                }
                c.add(tag);
            }
            HashMap<String, Double> N =  new HashMap<>();

            helper(c, N);

            for (String key : N.keySet()){
                N.put(key, N.get(key) / c.size());
            }
            Graph_2_AvgError_Target.add(N);
        }
        //System.out.println(Graph_2_AvgError_Target);
    }

    public static void Generate_Graph_2_Extra(){

        ArrayList<ArrayList<Integer>> T = new ArrayList<>();
        ArrayList<Integer> _T1 = new ArrayList<>();
        _T1.add(0);_T1.add(90);_T1.add(180);_T1.add(270);
        T.add(_T1);
        ArrayList<Integer> _T2 = new ArrayList<>();
        _T2.add(45);_T2.add(135);_T2.add(225);_T2.add(315);
        T.add(_T2);

        for (int i=0; i<2; i++) {

            ArrayList<HashMap<String,Double>> c = new ArrayList<>();

            for (Participant participant : participants) {
                HashMap<String, Double> tag = new HashMap<>();
                for (String HT : participant.AvgSpent_Time.keySet()) {
                    String[] parts = HT.split(":");
                    if (T.get(i).contains(Integer.parseInt(parts[0]))) {
                        if (!tag.containsKey(parts[1])) tag.put(parts[1], participant.AvgSpent_Time.get(HT));
                        else tag.put(parts[1], tag.get(parts[1]) + participant.AvgSpent_Time.get(HT));
                    }
                }
                for (String key : tag.keySet()) {
                    tag.put(key, tag.get(key) / 4);
                }
                c.add(tag);
            }

            HashMap<String, Double> N =  new HashMap<>();

            helper(c, N);

            for (String key : N.keySet()){
                N.put(key, N.get(key) / c.size());
            }

            Graph_2_Time_Target.add(N);

        }
        //System.out.println(Graph_2_Time_Target);
    }

    private static void helper(ArrayList<HashMap<String, Double>> c, HashMap<String, Double> n) {
        for (HashMap<String,Double> hashMap : c){
            for (String key : hashMap.keySet()){
                if (!n.containsKey(key)) n.put(key, hashMap.get(key));
                else n.put(key, n.get(key) + hashMap.get(key));
            }
        }
    }

    public static void OutputGenerator(){

        Output_Table1();
        Output_Table2();
        Output_Graph1();
        Output_Graph2();
        Output_Table1_Extra();
        Output_Table2_Extra();
        Output_Graph1_Extra();
        Output_Graph2_Extra();

    }

    public static void Output_Table1(){
        try {

            //System.out.println(Table_1_AvgError);

            ArrayList<Integer> n = new ArrayList<>();
            for (String key : Table_1_AvgError.keySet()){
                n.add(Integer.parseInt(key));
            }

            Collections.sort(n);

            FileWriter fw = new FileWriter("out/Output_Table1-"+filename);
            BufferedWriter bw = new BufferedWriter(fw);

            Date date = new Date();
            bw.write("N = "+participants.size()+","+date+"\n");

            for (Integer key : n){
                bw.write(key+","+Table_1_AvgError.get(key.toString())+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Output_Table2(){
        try {

            ArrayList<Integer> n = new ArrayList<>(Table_2_AvgError.keySet());
            Collections.sort(n);

            FileWriter fw = new FileWriter("out/Output_Table2-"+filename);
            BufferedWriter bw = new BufferedWriter(fw);

            Date date = new Date();
            bw.write("N = "+participants.size()+","+date+"\n");

            for (Integer key : n){
                bw.write(key+","+Table_2_AvgError.get(key)+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Output_Graph1(){
        try {

            ArrayList<Integer> n = new ArrayList<>();
            for (String key : Graph_1_AvgError_Heading.get("180").keySet()){
                n.add(Integer.parseInt(key));
                //System.out.println(key);
            }

            Collections.sort(n);

            FileWriter fw = new FileWriter("out/Output_Graph1-"+filename);
            BufferedWriter bw = new BufferedWriter(fw);

            Date date = new Date();
            bw.write("N = "+participants.size()+","+date+"\n");
            bw.write("heading,0degree,180degree\n");

            for (Integer key : n){
                bw.write(key+","+Graph_1_AvgError_Heading.get("0").get(key.toString())+","+Graph_1_AvgError_Heading.get("180").get(key.toString())+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Output_Graph2(){
        try {

            //System.out.println(Graph_2_AvgError_Target);

            ArrayList<Integer> n = new ArrayList<>();
            for (String key : Graph_2_AvgError_Target.get(0).keySet()){
                n.add(Integer.parseInt(key));
            }
            Collections.sort(n);

            FileWriter fw = new FileWriter("out/Output_Graph2-"+filename);
            BufferedWriter bw = new BufferedWriter(fw);

            Date date = new Date();
            bw.write("N = "+participants.size()+","+date+"\n");
            bw.write("target,heading[0;90;180;270],heading[45;135;225;315]\n");

            for (Integer key : n){
                bw.write(key+","+Graph_2_AvgError_Target.get(0).get(key.toString())+","+Graph_2_AvgError_Target.get(1).get(key.toString())+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Output_Table1_Extra(){
        try {

            //System.out.println(Table_1_AvgError);

            ArrayList<Integer> n = new ArrayList<>();
            for (String key : Table_1_Time.keySet()){
                n.add(Integer.parseInt(key));
            }

            Collections.sort(n);

            FileWriter fw = new FileWriter("out/Output_Table1_Extra-"+filename);
            BufferedWriter bw = new BufferedWriter(fw);

            Date date = new Date();
            bw.write("N = "+participants.size()+","+date+"\n");

            for (Integer key : n){
                bw.write(key+","+Table_1_Time.get(key.toString())+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Output_Table2_Extra(){
        try {

            ArrayList<Integer> n = new ArrayList<>(Table_2_Time.keySet());
            Collections.sort(n);

            FileWriter fw = new FileWriter("out/Output_Table2_Extra-"+filename);
            BufferedWriter bw = new BufferedWriter(fw);

            Date date = new Date();
            bw.write("N = "+participants.size()+","+date+"\n");

            for (Integer key : n){
                bw.write(key+","+Table_2_Time.get(key)+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Output_Graph1_Extra(){
        try {

            ArrayList<Integer> n = new ArrayList<>();
            for (String key : Graph_1_Time_Heading.get("0").keySet()){
                n.add(Integer.parseInt(key));
            }

            Collections.sort(n);

            FileWriter fw = new FileWriter("out/Output_Graph1_Extra-"+filename);
            BufferedWriter bw = new BufferedWriter(fw);

            Date date = new Date();
            bw.write("N = "+participants.size()+","+date+"\n");
            bw.write("heading,0degree,180degree\n");

            for (Integer key : n){
                bw.write(key+","+Graph_1_Time_Heading.get("0").get(key.toString())+","+Graph_1_Time_Heading.get("180").get(key.toString())+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Output_Graph2_Extra(){
        try {

            //System.out.println(Graph_2_AvgError_Target);
            ArrayList<Integer> n = new ArrayList<>();
            for (String key : Graph_2_Time_Target.get(0).keySet()){
                n.add(Integer.parseInt(key));
            }
            Collections.sort(n);

            FileWriter fw = new FileWriter("out/Output_Graph2_Extra-"+filename);
            BufferedWriter bw = new BufferedWriter(fw);
            Date date = new Date();
            bw.write("N = "+participants.size()+","+date+"\n");
            bw.write("target,heading[0;90;180;270],heading[45;135;225;315]\n");

            for (Integer key : n){
                bw.write(key+","+Graph_2_Time_Target.get(0).get(key.toString())+","+Graph_2_Time_Target.get(1).get(key.toString())+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(Graph_2_Time_Target);
    }

    public static void Reference_Generator(){

        ArrayList<String> angle = new ArrayList<String>() {{
            add("0");
            add("45");
            add("90");
            add("135");
            add("180");
            add("225");
            add("270");
            add("315");
        }};

        try {

            FileWriter fw = new FileWriter("out/REFs"+filename);
            BufferedWriter bw = new BufferedWriter(fw);
            Date date = new Date();
            bw.write("Session_id,0,45,90,135,180,225,270,315\n");
            for (Participant participant : participants){
                String s = participant.sessionID+"";
                for (String ref : angle){
                    if (participant.ReferenceDirection.contains(ref)) s = s.concat(",1");
                    else s = s.concat(",0");
                }
                bw.write(s+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void Generate_Graph_2_SP(){

        ArrayList<ArrayList<Integer>> T = new ArrayList<>();
        ArrayList<Integer> _T1 = new ArrayList<>();
        _T1.add(0);_T1.add(180);
        ArrayList<Integer> _T2 = new ArrayList<>();
        _T2.add(45);_T2.add(135);_T2.add(225);_T2.add(315);_T2.add(90);_T2.add(270);
        T.add(_T1);
        T.add(_T2);

        for (int i=0; i<2; i++) {
            ArrayList<HashMap<String,Double>> c = new ArrayList<>();
            for (Participant participant : participants) {
                HashMap<String, Double> tag = new HashMap<>();
                for (String HT : participant.AvgPointingError.keySet()) {
                    String[] parts = HT.split(":");
                    if (T.get(i).contains(Integer.parseInt(parts[0]))) {
                        if (!tag.containsKey(parts[1])) tag.put(parts[1], participant.AvgPointingError.get(HT));
                        else tag.put(parts[1], tag.get(parts[1]) + participant.AvgPointingError.get(HT));
                    }
                }


                for (String key : tag.keySet()) {
                    if (i!=0) tag.put(key, tag.get(key) / 6);
                    else tag.put(key, tag.get(key) / 2);

                }
                c.add(tag);
            }
            HashMap<String, Double> N =  new HashMap<>();

            helper(c, N);

            for (String key : N.keySet()){
                N.put(key, N.get(key) / c.size());
            }
            Graph_2_AvgError_Target_sp.add(N);
        }
        //System.out.println(Graph_2_AvgError_Target);
    }

    public static void Output_G2_SP(){
        try {

            //System.out.println(Graph_2_AvgError_Target);

            ArrayList<Integer> n = new ArrayList<>();
            for (String key : Graph_2_AvgError_Target_sp.get(0).keySet()){
                n.add(Integer.parseInt(key));
            }
            Collections.sort(n);

            FileWriter fw = new FileWriter("out/Output_Graph2_SP-"+filename);
            BufferedWriter bw = new BufferedWriter(fw);

            Date date = new Date();
            bw.write("N = "+participants.size()+","+date+"\n");
            bw.write("target,heading[0;180],heading[the rest]\n");

            for (Integer key : n){
                bw.write(key+","+Graph_2_AvgError_Target_sp.get(0).get(key.toString())+","+Graph_2_AvgError_Target_sp.get(1).get(key.toString())+"\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

