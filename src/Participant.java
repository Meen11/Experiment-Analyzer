import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Participant {

    int ID;
    int sessionID ;
    HashMap<String,ArrayList<Double>> PointingError;      // to store the pointing value for each trail indicating by object position.
    HashMap<String,Double> AvgPointingError;
    HashMap<String,Double> AvgPointingError_Each_Heading;
    HashMap<String,Double> AvgPointingError_Each_Target;
    ArrayList<String> ReferenceDirection;
    ArrayList<HashMap<String,Double>> ErrorPointing_Judgment;
    HashMap<String,ArrayList<Double>> Spent_Time;
    HashMap<String,Double> AvgSpent_Time;
    HashMap<String,Double> AvgSpentTime_Each_Heading;
    HashMap<String,Double> AvgSpentTime_Each_Target;
    ArrayList<String> ReferenceDirection_byTime;
    ArrayList<HashMap<String,Double>> Time_Judgment;
    double StdDev_EachHeading;
    double StdDev_Time_EachHeading;
    String ViewPoint;


    public Participant(int ID, int sessionID, String ViewPoint){
        this.ID = ID;
        this.sessionID = sessionID;
        this.ViewPoint = ViewPoint;
        PointingError = new HashMap<>();
        AvgPointingError = new HashMap<>();
        AvgPointingError_Each_Heading = new HashMap<>();
        AvgPointingError_Each_Target = new HashMap<>();
        ReferenceDirection = new ArrayList<>();
        ErrorPointing_Judgment = new ArrayList<>();
        this.StdDev_EachHeading = 0;

        Spent_Time = new HashMap<>();
        AvgSpent_Time = new HashMap<>();
        AvgSpentTime_Each_Heading = new HashMap<>();
        AvgSpentTime_Each_Target = new HashMap<>();
        ReferenceDirection_byTime = new ArrayList<>();
        Time_Judgment = new ArrayList<>();
        this.StdDev_Time_EachHeading = 0;
    }

    public void Calculate_AvgPointingError(){
        double sumError = 0;
        for (String key : PointingError.keySet()){
            for (Double n : PointingError.get(key)){
                sumError += n;
            }
            double avgError = sumError / PointingError.get(key).size();
            AvgPointingError.put(key,avgError);
            sumError = 0;
        }
    }

    public void Calculate_AvgSpent_Time(){
        double sumTime = 0;
        for (String key : Spent_Time.keySet()){
            for (Double n : Spent_Time.get(key)){
                sumTime += n;
            }
            double avgError = sumTime / Spent_Time.get(key).size();
            AvgSpent_Time.put(key,avgError);
            sumTime = 0;
        }
        //System.out.println(Spent_Time.get("135:315"));
        //System.out.println(AvgSpent_Time.get("135:315"));
    }

    public void Calculate_StdDev_EachHeading(){
        double[] data = new double[AvgPointingError_Each_Heading.size()];
        int i=0;
        for (String key : AvgPointingError_Each_Heading.keySet()){
            data[i++] = AvgPointingError_Each_Heading.get(key);
            //System.out.println(AvgPointingError_Each_Heading.get(key));
        }
        Statistics stat = new Statistics(data);
        this.StdDev_EachHeading = stat.getStdDev();
        //System.out.println(ID+" "+StdDev);
    }

    public void Calculate_StdDev_Time_EachHeading(){
        double[] data = new double[AvgSpentTime_Each_Heading.size()];
        int i=0;
        for (String key : AvgSpentTime_Each_Heading.keySet()){
            data[i++] = AvgSpentTime_Each_Heading.get(key);
            //System.out.println(AvgPointingError_Each_Heading.get(key));
        }
        Statistics stat = new Statistics(data);
        this.StdDev_Time_EachHeading = stat.getStdDev();
        //System.out.println(ID+" "+StdDev_Time_EachHeading);
    }

    public void Calculate_AvgPointingError_Each_Heading() {
        for (String key : AvgPointingError.keySet()){
            String[] parts = key.split(":");
            if (!AvgPointingError_Each_Heading.containsKey(parts[0])){
                AvgPointingError_Each_Heading.put(parts[0], AvgPointingError.get(key));
            }
            else {
                AvgPointingError_Each_Heading.put(parts[0], AvgPointingError_Each_Heading.get(parts[0]) + AvgPointingError.get(key));
            }
        }
        for (String key : AvgPointingError_Each_Heading.keySet()){
            AvgPointingError_Each_Heading.put(key, AvgPointingError_Each_Heading.get(key) / 8);     // Each heading direction has 8 different target directions.
        }
    }

    public void Calculate_AvgSpentTime_Each_Heading() {
        for (String key : AvgSpent_Time.keySet()){
            String[] parts = key.split(":");
            if (!AvgSpentTime_Each_Heading.containsKey(parts[0])){
                AvgSpentTime_Each_Heading.put(parts[0], AvgSpent_Time.get(key));
            }
            else {
                AvgSpentTime_Each_Heading.put(parts[0], AvgSpentTime_Each_Heading.get(parts[0]) + AvgSpent_Time.get(key));
            }
        }
        for (String key : AvgSpentTime_Each_Heading.keySet()){
            AvgSpentTime_Each_Heading.put(key, AvgSpentTime_Each_Heading.get(key) / 8);     // Each heading direction has 8 different target directions.
        }
        //System.out.println(AvgSpentTime_Each_Heading);
    }

    public void Calculate_AvgPointingError_Each_Target() {
        for (String key : AvgPointingError.keySet()){
            String[] parts = key.split(":");
            if (!AvgPointingError_Each_Target.containsKey(parts[1])){
                AvgPointingError_Each_Target.put(parts[1], AvgPointingError.get(key));
            }
            else {
                AvgPointingError_Each_Target.put(parts[1], AvgPointingError_Each_Heading.get(parts[1]) + AvgPointingError.get(key));
            }
        }
        for (String key : AvgPointingError_Each_Target.keySet()){
            AvgPointingError_Each_Target.put(key, AvgPointingError_Each_Target.get(key) / 8);     // Each heading direction has 8 different target directions.
        }
    }

    public void Calculate_AvgSpentTime_Each_Target() {
        for (String key : AvgSpent_Time.keySet()){
            String[] parts = key.split(":");
            if (!AvgSpentTime_Each_Target.containsKey(parts[1])){
                AvgSpentTime_Each_Target.put(parts[1], AvgSpent_Time.get(key));
            }
            else {
                AvgSpentTime_Each_Target.put(parts[1], AvgSpentTime_Each_Target.get(parts[1]) + AvgSpent_Time.get(key));
            }
        }
        for (String key : AvgSpentTime_Each_Target.keySet()){
            AvgSpentTime_Each_Target.put(key, AvgSpentTime_Each_Target.get(key) / 8);     // Each heading direction has 8 different target directions.
        }
        //System.out.println(AvgSpentTime_Each_Target);
    }

    public void Calculate_ReferenceDirection() {

        ArrayList<Double> err_head = new ArrayList<>();
        for (String key : AvgPointingError_Each_Heading.keySet()){
            err_head.add(AvgPointingError_Each_Heading.get(key));
        }
        Collections.sort(err_head,Collections.reverseOrder());

        double[] data = new double[4];
        for (int i=0; i<4; i++){
            data[i] = err_head.get(i);
        }

        Statistics stat = new Statistics(data);
        Double stdDev_worst = stat.getStdDev();

        double bestError = Double.MAX_VALUE;
        //System.out.println(AvgPointingError_Each_Heading);
        for (String key : AvgPointingError_Each_Heading.keySet()){
            if (AvgPointingError_Each_Heading.get(key) < bestError) bestError = AvgPointingError_Each_Heading.get(key);
        }

        double checker = bestError + (1.5 * stdDev_worst);    // any hading angle which it's pointing error has value within the value of bestError + 1.5StdDev is considered as a reference direction.
        //System.out.println(bestError+" + 1.5("+StdDev_EachHeading+") = "+bestError+" + "+1.5*StdDev_EachHeading+" = "+checker);

        for (String key : AvgPointingError_Each_Heading.keySet()){
            //System.out.println(ID+" "+AvgPointingError_Each_Heading.get(key)+" ? "+checker);
            if (AvgPointingError_Each_Heading.get(key) <= checker) {
                String[] parts = key.split(":");
                ReferenceDirection.add(parts[0]);
            }
        }
        //System.out.println(ID+" : "+ReferenceDirection);
    }

    public void Calculate_ReferenceDirection_byTime() {
        double bestTime = Double.MAX_VALUE;
        //System.out.println(AvgSpentTime_Each_Heading);
        for (String key : AvgSpentTime_Each_Heading.keySet()){
            if (AvgSpentTime_Each_Heading.get(key) < bestTime) bestTime = AvgSpentTime_Each_Heading.get(key);
        }

        double checker = bestTime + (1.5 * StdDev_Time_EachHeading);    // any hading angle which it's pointing error has value within the value of bestError + 1.5StdDev is considered as a reference direction.
        //System.out.println(bestTime+" + 1.5("+StdDev_Time_EachHeading+") = "+bestTime+" + "+1.5*StdDev_Time_EachHeading+" = "+checker);

        for (String key : AvgSpentTime_Each_Heading.keySet()){
            //System.out.println(ID+" "+AvgSpentTime_Each_Heading.get(key)+" ? "+checker);
            if (AvgSpentTime_Each_Heading.get(key) <= checker) {
                String[] parts = key.split(":");
                ReferenceDirection_byTime.add(parts[0]);
            }
        }
        //System.out.println(ID+" : "+ReferenceDirection_byTime);
    }

    public void Calculate_ErrorPointing_Judgment() {
        for (int i=0; i<4 ;i++){
          HashMap<String,Double> n = new HashMap<>();
          ErrorPointing_Judgment.add(n);
       }

       for (String key : AvgPointingError.keySet()){
           String parts[] = key.split(":");
           if (ReferenceDirection.contains(parts[0]) && ReferenceDirection.contains(parts[1])){
               ErrorPointing_Judgment.get(0).put(key,AvgPointingError.get(key));    // the 1st relative judgment group.
           }
           else if (ReferenceDirection.contains(parts[0]) && !ReferenceDirection.contains(parts[1])){
               ErrorPointing_Judgment.get(1).put(key,AvgPointingError.get(key));    // the 2nd relative judgment group.
           }
           else if (!ReferenceDirection.contains(parts[0]) && ReferenceDirection.contains(parts[1])){
               ErrorPointing_Judgment.get(2).put(key,AvgPointingError.get(key));    // the 3rd relative judgment group.
           }
           else {
               ErrorPointing_Judgment.get(3).put(key,AvgPointingError.get(key));    // the 4th relative judgment group.
           }
       }
//        System.out.println(ErrorPointing_Judgment.get(0));
//        System.out.println(ErrorPointing_Judgment.get(1));
//        System.out.println(ErrorPointing_Judgment.get(2));
//        System.out.println(ErrorPointing_Judgment.get(3));
    }

    public void Calculate_Time_Judgment() {
        for (int i=0; i<4 ;i++){
            HashMap<String,Double> n = new HashMap<>();
            Time_Judgment.add(n);
        }

        for (String key : AvgSpent_Time.keySet()){
            String parts[] = key.split(":");
            if (ReferenceDirection_byTime.contains(parts[0]) && ReferenceDirection_byTime.contains(parts[1])){
                Time_Judgment.get(0).put(key,AvgSpent_Time.get(key));    // the 1st relative judgment group.
            }
            else if (ReferenceDirection_byTime.contains(parts[0]) && !ReferenceDirection_byTime.contains(parts[1])){
                Time_Judgment.get(1).put(key,AvgSpent_Time.get(key));    // the 2nd relative judgment group.
            }
            else if (!ReferenceDirection_byTime.contains(parts[0]) && ReferenceDirection_byTime.contains(parts[1])){
                Time_Judgment.get(2).put(key,AvgSpent_Time.get(key));    // the 3rd relative judgment group.
            }
            else {
                Time_Judgment.get(3).put(key,AvgSpent_Time.get(key));    // the 4th relative judgment group.
            }
        }
//        System.out.println(Time_Judgment.get(0));
//        System.out.println(Time_Judgment.get(1));
//        System.out.println(Time_Judgment.get(2));
//        System.out.println(Time_Judgment.get(3));
    }

    public void Calculate_For_G2(){

    }

}
