package com.example.a8magicball;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class AnimationActivity extends AppCompatActivity implements View.OnClickListener {

    public ImageView ejex;
    public ImageView ejey;
    public ImageView alpha;
    public ImageView rotation;
    public ImageView all;
    public ImageView bubble;
    public ImageView scale;

    public Button btnejex;
    public Button btnejey;
    public Button btnalpha;
    public Button btnrotation;
    public Button btnall;
    public Button btnbubble;
    public Button btnscale;

    public ObjectAnimator anejex;
    public ObjectAnimator anejey;
    public ObjectAnimator analpha;
    public ObjectAnimator anrotation;
    public ObjectAnimator anall;

    public long duration= 5000;

    public AnimatorSet animatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        btnejex=findViewById(R.id.btnejex);
        btnejey=findViewById(R.id.btnejey);
        btnalpha=findViewById(R.id.btnalpha);
        btnrotation=findViewById(R.id.btnrotation);
        btnall=findViewById(R.id.btntodo);
        btnbubble=findViewById(R.id.btnbubble);
        btnscale=findViewById(R.id.btnscale);

        ejex=findViewById(R.id.imgvejex);
        ejey=findViewById(R.id.imgvejey);
        alpha=findViewById(R.id.imgvalpha);
        rotation=findViewById(R.id.imgvrotation);
        all=findViewById(R.id.imgvtodo);
        bubble=findViewById(R.id.imgvbubble);
        scale=findViewById(R.id.imgvscale);

        btnejex.setOnClickListener(this);
        btnejey.setOnClickListener(this);
        btnalpha.setOnClickListener(this);
        btnrotation.setOnClickListener(this);
        btnall.setOnClickListener(this);
        btnbubble.setOnClickListener(this);
        btnscale.setOnClickListener(this);

        ejex.setOnClickListener(this);
        ejey.setOnClickListener(this);
        alpha.setOnClickListener(this);
        rotation.setOnClickListener(this);
        all.setOnClickListener(this);
        bubble.setOnClickListener(this);
        scale.setOnClickListener(this);


    }

    @Override
    public void onClick (View v){
        Intent intent;

        switch (v.getId())
        {
            case R.id.btnejex:

                anejex= ObjectAnimator.ofFloat(ejex,"x",0f,500f);
                anejex.setDuration(duration);
                AnimatorSet animatorSetX= new AnimatorSet();
                animatorSetX.play(anejex);
                animatorSetX.start();

                break;

            case R.id.btnejey:

                anejey= ObjectAnimator.ofFloat(ejey,"y",0f,500f);
                anejey.setDuration(duration);
                AnimatorSet animatorSetY= new AnimatorSet();
                animatorSetY.play(anejey);
                animatorSetY.start();

                break;
            case R.id.btnalpha:

                analpha= ObjectAnimator.ofFloat(alpha,View.ALPHA,1.0f , 0.0f);
                analpha.setDuration(duration);
                AnimatorSet animatorSetAlpha= new AnimatorSet();
                animatorSetAlpha.play(analpha);
                animatorSetAlpha.start();

                break;

            case R.id.btnrotation:

                anrotation= ObjectAnimator.ofFloat(rotation,"rotation",0f , 360f);
                anrotation.setDuration(3000);
                AnimatorSet animatorSetRotation= new AnimatorSet();
                animatorSetRotation.play(anrotation);
                animatorSetRotation.start();

                break;

            case R.id.btntodo:

                analpha= ObjectAnimator.ofFloat(all,View.ALPHA,1.0f , 0.0f);
                analpha.setDuration(duration);

                anrotation= ObjectAnimator.ofFloat(all,"rotation",0f , 360f);
                anrotation.setDuration(duration);

                anejex= ObjectAnimator.ofFloat(all,"x",0f,500f);
                anejex.setDuration(duration);

                AnimatorSet animatorSetTodo= new AnimatorSet();
                animatorSetTodo.playTogether(analpha,anrotation,anejex);
                animatorSetTodo.start();

                break;


            case R.id.btnbubble:

                anrotation= ObjectAnimator.ofFloat(bubble,"rotation",0f , 360f);
                anrotation.setDuration(1000);
                AnimatorSet animatorSetBubble= new AnimatorSet();
                animatorSetBubble.play(anrotation);
                animatorSetBubble.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                animation.start();
                }
                });

            animatorSetBubble.start();

                break;

            case R.id.btnscale:

                Animation animationscale= AnimationUtils.loadAnimation(this,R.anim.scale);
                scale.startAnimation(animationscale);

                break;


        }

    }
}
