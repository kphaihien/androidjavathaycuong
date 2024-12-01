package com.example.btladr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;


public class PlayGame extends AppCompatActivity implements View.OnClickListener{
    
    private TextView tvQuestion;
    private TextView tvContentQuestion;
    private TextView tvAnswer1,tvAnswer2,tvAnswer3,tvAnswer4;
    private TextView tvPoint;

    private String userName;
    private User user;
    private Date formattedPlayDate;

    private boolean isMusicPlaying=true;
    private SharedPreferences preferences;

    private List<Question> mListQuestion;
    private Question mQuestion;
    private int currentQuestion=0;
    private int currentPoint=0;

    Connection connection;
    ResultSet resultSet;
    Statement statement;
    String queryToGetDataQuestions="SELECT q.question_id, q.content AS questionContent, \n" +
            "                   a.answer_id AS answerId, a.content AS answerContent, a.is_correct,a.true_hint as true_hint\n" +
            "            FROM questions q\n" +
            "            JOIN answers a ON q.question_id = a.question_id;";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_game);
        initUi();
        resultSet=startConnect(queryToGetDataQuestions);
        showUsernameDialog();
        playBackGroundMusic();

        //Set du lieu cau hoi
        mListQuestion=getListQuestion1();
        if(mListQuestion.isEmpty()){
            return;
        }
        setDataQuestion(mListQuestion.get(currentQuestion));

        //Quay ve man hinh chinh
        TextView btnGoBack=findViewById(R.id.back);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(PlayGame.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }



    private void playBackGroundMusic(){
        preferences=getSharedPreferences("musicPrefs",MODE_PRIVATE);
        isMusicPlaying=preferences.getBoolean("isMusicPlaying",true);
        TextView toogleMusic=findViewById(R.id.speaker);
        toogleMusic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(isMusicPlaying){
                    Intent serviceIntent=new Intent(PlayGame.this, BackGroundMusic.class);
                    stopService(serviceIntent);
                    Drawable drawable=getResources().getDrawable(R.drawable.loudspeaker_off);
                    toogleMusic.setBackground(drawable);
                }else{
                    Intent serviceIntent=new Intent(PlayGame.this, BackGroundMusic.class);
                    startService(serviceIntent);
                    Drawable drawable=getResources().getDrawable(R.drawable.loudspeaker_on);
                    toogleMusic.setBackground(drawable);
                }
                isMusicPlaying=!isMusicPlaying;
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("isMusicPlaying",isMusicPlaying);
                editor.apply();
            }
        });
    }

    //Hàm yêu cầu người dùng nhập tên khi vào game
    private void showUsernameDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.user_profile);
        EditText usernameEditText = dialog.findViewById(R.id.playerName);
        Button confirmButton = dialog.findViewById(R.id.confirmButton);

        // Thiết lập sự kiện cho nút Xác nhận
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = usernameEditText.getText().toString().trim();

                // Kiểm tra xem tên người dùng có trống không
                if (!inputUsername.isEmpty()) {
                    userName = inputUsername;  // Lưu giá trị username
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        formattedPlayDate=Date.valueOf(String.valueOf(LocalDate.now()));
                    }
                    dialog.dismiss();

                } else {
                    // Thông báo nếu username trống
                    usernameEditText.setError("Vui lòng nhập tên người dùng");
                }
            }
        });

        // Buộc người dùng phải nhập tên, không cho đóng dialog bằng nút back hoặc bấm ngoài
        dialog.setCancelable(false);

        // Hiển thị dialog
        dialog.show();
    }




    // Hàm để kết nối đến SQLServer và thực hiện 1 query
    private ResultSet startConnect(String query){
        ConnectionSQL c=new ConnectionSQL();
        connection= c.conclass();
        if (connection != null) {
            try {
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = statement.executeQuery(query);
            } catch (Exception e) {
                Log.e("Error:", e.getMessage());
            }
        }
        return resultSet;

    }

    //Hàm đẩy dữ liệu người chơi lên database
    private void pushUserData(){
        ConnectionSQL c=new ConnectionSQL();
        connection=c.conclass();
        if(connection!=null){
            try{
                String query="insert into userRecords values('"+ userName +"',"+currentPoint+",'"+formattedPlayDate.toString()+"')";
                statement=connection.createStatement();
                int rowsAffected=statement.executeUpdate(query);
            }catch(Exception e){
                Log.e("Error:",e.getMessage());
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }


    //Hàm set câu hỏi cho các TextView
    private void setDataQuestion(Question question) {
        if (question==null){
            return;
        }
        mQuestion=question;

        tvAnswer1.setBackgroundResource(R.drawable.button_background);
        tvAnswer2.setBackgroundResource(R.drawable.button_background);
        tvAnswer3.setBackgroundResource(R.drawable.button_background);
        tvAnswer4.setBackgroundResource(R.drawable.button_background);

        String tittleQuestion="Question "+question.getNumber();
        tvQuestion.setText(tittleQuestion);
        tvContentQuestion.setText(question.getContent());
        tvAnswer1.setText(question.getListAnswer().get(0).getContent());
        tvAnswer2.setText(question.getListAnswer().get(1).getContent());
        tvAnswer3.setText(question.getListAnswer().get(2).getContent());
        tvAnswer4.setText(question.getListAnswer().get(3).getContent());

        tvAnswer1.setOnClickListener(this);
        tvAnswer2.setOnClickListener(this);
        tvAnswer3.setOnClickListener(this);
        tvAnswer4.setOnClickListener(this);
    }


    //Hàm định nghĩa giao diện khi vừa vào trò chơi(UI)
    private void initUi(){
        tvQuestion=findViewById(R.id.txt_question);
        tvContentQuestion=findViewById(R.id.txt_contentquestion);
        tvAnswer1=findViewById(R.id.txtAnswer1);
        tvAnswer2=findViewById(R.id.txtAnswer2);
        tvAnswer3=findViewById(R.id.txtAnswer3);
        tvAnswer4=findViewById(R.id.txtAnswer4);
        tvPoint=findViewById(R.id.point);
    }

    //Hàm lấy ra câu hỏi từ resultSet
    private List<Question> getListQuestion1(){
        Random random=new Random();
        List<Question> list=new ArrayList<>();
        Set<Integer> selectedQuestions=new HashSet<>();
        try {
            if (resultSet != null && resultSet.next()) {
                // Di chuyển con trỏ ResultSet về dòng đầu tiên
                resultSet.beforeFirst(); // Đảm bảo con trỏ ở trước dòng đầu tiên
                // Lấy tổng số dòng của resultSet
                resultSet.last();  // Di chuyển con trỏ đến dòng cuối cùng để đếm
                int totalRows = resultSet.getRow();
                resultSet.beforeFirst();  // Di chuyển con trỏ trở lại trước dòng đầu tiên
                int numberOfQuestions = totalRows / 4;  // Giả sử mỗi câu hỏi có 4 câu trả lời
                // Lặp qua các câu hỏi
                while (list.size() < numberOfQuestions) {
                    // Chọn ngẫu nhiên một dòng, nhưng đảm bảo mỗi câu hỏi có 4 câu trả lời
                    int randomQuestionStartRow = random.nextInt(numberOfQuestions) * 4 + 1;  // Chọn ngẫu nhiên câu hỏi

                    if(!selectedQuestions.contains(randomQuestionStartRow)) {
                        selectedQuestions.add(randomQuestionStartRow);
                        // Di chuyển con trỏ đến dòng bắt đầu của câu hỏi
                        resultSet.absolute(randomQuestionStartRow);
                        String questionContent = resultSet.getString("questionContent");
                        // Lấy 4 dòng tiếp theo cho 4 câu trả lời
                        List<Answer> answerList = new ArrayList<>();
                        String truehint = "";
                        for (int j = 0; j < 4; j++) {
                            boolean is_correct = resultSet.getBoolean("is_correct");
                            if (is_correct) {
                                truehint = resultSet.getString("true_hint");
                            }
                            String answerContent = resultSet.getString("answerContent");
                            answerList.add(new Answer(is_correct, answerContent));
                            resultSet.next();
                        }
                        // Thêm câu hỏi vào danh sách với số thứ tự i+1
                        list.add(new Question(answerList, questionContent, list.size() + 1, truehint));
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return list;
    }



    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId==R.id.txtAnswer1){
            tvAnswer1.setBackgroundResource(R.drawable.background_orange_corner_30);
            checkAnswer(tvAnswer1,mQuestion,mQuestion.getListAnswer().get(0));
        } else if (viewId==R.id.txtAnswer2) {
            tvAnswer2.setBackgroundResource(R.drawable.background_orange_corner_30);
            checkAnswer(tvAnswer2,mQuestion,mQuestion.getListAnswer().get(1));
        }else if (viewId==R.id.txtAnswer3) {
            tvAnswer3.setBackgroundResource(R.drawable.background_orange_corner_30);
            checkAnswer(tvAnswer3,mQuestion,mQuestion.getListAnswer().get(2));
        }else if (viewId==R.id.txtAnswer4) {
            tvAnswer4.setBackgroundResource(R.drawable.background_orange_corner_30);
            checkAnswer(tvAnswer4,mQuestion,mQuestion.getListAnswer().get(3));
        }
    }



    //Hàm kiểm tra câu hỏi xem có đúng hay không
    public void checkAnswer(TextView textView,Question question,Answer answer){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(answer.isCorrect()){
                    textView.setBackgroundResource(R.drawable.bg_green);
                    playCorrectSound();
                    increasePoint();
                    showTrueAnswerDialog(mQuestion.getTruehint());
                    nextQuestion();
                }else{
                    textView.setBackgroundResource(R.drawable.bg_red);
                    showAnswerCorrect(question);
                    playFalseSound();
                    gameOver();
                }
            }
        },1000);
    }

    public void increasePoint(){
        currentPoint+=10;
        tvPoint.setText("Points: "+currentPoint);
    }

    private void showAnswerCorrect(Question question) {
        if(question==null || question.getListAnswer()==null||question.getListAnswer().isEmpty()){
            return;
        }

        if(question.getListAnswer().get(0).isCorrect()){
            tvAnswer1.setBackgroundResource(R.drawable.bg_green);
        } else if (question.getListAnswer().get(1).isCorrect()) {
            tvAnswer2.setBackgroundResource(R.drawable.bg_green);
        }else if (question.getListAnswer().get(2).isCorrect()) {
            tvAnswer3.setBackgroundResource(R.drawable.bg_green);
        }else if (question.getListAnswer().get(3).isCorrect()) {
            tvAnswer4.setBackgroundResource(R.drawable.bg_green);
        }
    }


    //Hàm chuyển sang câu hỏi tiếp theo
    private void nextQuestion(){
        if(currentQuestion==mListQuestion.size()-1){
            showDialog("Bạn đã chiến thắng!!!\nSố điểm của bạn là: "+currentPoint);
            pushUserData();
        }else{
            currentQuestion++;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDataQuestion(mListQuestion.get(currentQuestion));
                }
            },1000);
        }
    }



    private void gameOver(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDialog("Hơi đen cho bạn rồi\nSố điểm của bạn là: "+currentPoint);
                pushUserData();
            }
        },1000);
    }


    private void playFalseSound(){
        MediaPlayer mediaPlayer;
        mediaPlayer=MediaPlayer.create(this,R.raw.wrong_sound);
        mediaPlayer.setVolume(1f,1f);
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
    }

    private void playCorrectSound(){
        MediaPlayer mediaPlayer;
        mediaPlayer=MediaPlayer.create(this,R.raw.correct_sound);
        mediaPlayer.setVolume(1f,1f);

        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
    }

    //Hàm in ra lời chúc mừng khi trả lời đúng
    private void showTrueAnswerDialog(String message){
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.test_custom_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.border_for_dialog);
        Button btnNext=dialog.findViewById(R.id.buttonnexttest);
        TextView tv=dialog.findViewById(R.id.textOverlay);
        tv.setText(message);
        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }

        });
        dialog.show();
    }



    private void showDialog(String message){
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.background_for_win_lose,null);

        TextView dialogMessage=dialogView.findViewById(R.id.dialog_message);
        dialogMessage.setText(message);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setPositiveButton("Chơi lại!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                currentQuestion=0;
                setDataQuestion(mListQuestion.get(currentQuestion));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Về màn hình chính", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(PlayGame.this,MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
