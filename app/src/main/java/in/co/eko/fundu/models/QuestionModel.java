package in.co.eko.fundu.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rahul on 12/13/16.
 */
//{
//        "question_id": "Q101",
//        "question_value": "What's your Identity Card Number?",
//        "answer_mode": "text",
//        "answer_check": "Numeric",
//        "answer_min_length": 7,
//        "answer_max_length": "15"
//        }
public class QuestionModel implements Serializable {

    @SerializedName("question_id")
    String question_id;
    @SerializedName("question_value")
    String question_value;
    @SerializedName("answer_mode")
    String answer_mode;
    @SerializedName("answer_check")
    String answer_check;
    @SerializedName("answer_min_length")
    String answer_min_length;
    @SerializedName("answer_max_length")
    String answer_max_length;
    @SerializedName("place_holder")
    String place_holder;



    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_value() {
        return question_value;
    }

    public void setQuestion_value(String question_value) {
        this.question_value = question_value;
    }

    public String getAnswer_mode() {
        return answer_mode;
    }

    public void setAnswer_mode(String answer_mode) {
        this.answer_mode = answer_mode;
    }

    public String getAnswer_check() {
        return answer_check;
    }

    public void setAnswer_check(String answer_check) {
        this.answer_check = answer_check;
    }

    public String getAnswer_min_length() {
        return answer_min_length;
    }

    public void setAnswer_min_length(String answer_min_length) {
        this.answer_min_length = answer_min_length;
    }

    public String getAnswer_max_length() {
        return answer_max_length;
    }

    public void setAnswer_max_length(String answer_max_length) {
        this.answer_max_length = answer_max_length;
    }
    public String getPlace_holder() {
        return place_holder;
    }

    public void setPlace_holder(String place_holder) {
        this.place_holder = place_holder;
    }

}

