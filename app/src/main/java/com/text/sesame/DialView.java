package com.text.sesame;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DialView extends View {

    /**
     * 圆弧的度数
     */
    private final static float TOTAL_ANGLE = 225.0f;
    /**
     * 刻度圆弧有23个点
     */
    private final static int SCALE_COUNT = 20;

    private final static String CREDIT_LEVEL[] = {"信用较差", "信用中等", "信用良好", "信用优秀", "信用极好"};

    private final static String BETA = "";

    private final static String EVALUATION_TIME = "评估时间:";

    private final static int MIN_ALPHA = 0;

    private final static int MAX_ALPHA = 255;

    private final static int RED = 255;

    private final static int GREEN = 255;

    private final static int BLUE = 255;

    private final static int COLOR_TRANSPARENT = Color.argb(MIN_ALPHA, RED, GREEN, BLUE);

    private final static int COLOR_WHITE = Color.argb(MAX_ALPHA, RED, GREEN, BLUE);
    /**
     * 渐变进度圆弧的颜色
     */
    private final static int GRADIENT_COLORS[] = {COLOR_TRANSPARENT, COLOR_TRANSPARENT, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE};
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 刻度圆弧的半径
     */
    private int mScaleArcRadius;
    /**
     * 刻度圆弧的宽度
     */
    private int mScaleArcWidth;
    /**
     * 进度圆弧的半径
     */
    private int mProgressArcRadius;
    /**
     * 进度圆弧的宽度
     */
    private int mProgressArcWidth;
    /**
     * 进度圆弧上的小圆点的半径
     */
    private int mBallOverstepWidth;
    /**
     * BETA的字体大小
     */
    private int mBetaTextSize;
    /**
     * 信用级别的字体大小
     */
    private int mCreditLevelTextSize;
    /**
     * 信用分数的字体大小
     */
    private int mCreditScoreTextSize;
    /**
     * 评估时间的字体大小
     */
    private int mEvaluationTimeTextSize;
    /**
     * 字上下行的间隔
     */
    private int mTextSpacing;
    /**
     * 箭头与圆弧的间隔
     */
    private int mArrowSpacing;
    /**
     * 信用分数
     */
    private int mCreditScore = 666;

    public DialView(Context context) {
        this(context, null);
    }

    public DialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttr(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    /**
     * 解析属性
     *
     * @param context      Context
     * @param attrs        AttributeSet
     * @param defStyleAttr defStyleAttr
     */
    private void parseAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray _TypedArray = context.obtainStyledAttributes(attrs, R.styleable.DialView, defStyleAttr, 0);
        mScaleArcRadius = _TypedArray.getDimensionPixelSize(R.styleable.DialView_scaleArcRadius, dp2px(context, 100));
        mScaleArcWidth = _TypedArray.getDimensionPixelSize(R.styleable.DialView_scaleArcWidth, dp2px(context, 2));
        mProgressArcRadius = _TypedArray.getDimensionPixelSize(R.styleable.DialView_progressArcRadius, dp2px(context, 105));
        mProgressArcWidth = _TypedArray.getDimensionPixelSize(R.styleable.DialView_progressArcWidth, dp2px(context, 1));
        mBetaTextSize = _TypedArray.getDimensionPixelSize(R.styleable.DialView_betaTextSize, dp2px(context, 12));
        mCreditLevelTextSize = _TypedArray.getDimensionPixelSize(R.styleable.DialView_creditLevelTextSize, dp2px(context, 18));
        mCreditScoreTextSize = _TypedArray.getDimensionPixelSize(R.styleable.DialView_creditScoreTextSize, dp2px(context, 40));
        mEvaluationTimeTextSize = _TypedArray.getDimensionPixelSize(R.styleable.DialView_evaluationTimeTextSize, dp2px(context, 12));
        mTextSpacing = _TypedArray.getDimensionPixelSize(R.styleable.DialView_textSpacing, dp2px(context, 12));
        mArrowSpacing = _TypedArray.getDimensionPixelSize(R.styleable.DialView_arrowSpacing, dp2px(context, 5));
        _TypedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int _WidthMode= MeasureSpec.getMode(widthMeasureSpec);
        int _HeightMode = MeasureSpec.getMode(heightMeasureSpec);
        Bitmap _Ball = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        mBallOverstepWidth =(int) Math.ceil(_Ball.getHeight() / 2.0 - mProgressArcWidth / 2.0);

        if (_WidthMode != MeasureSpec.EXACTLY) {
            int _Width = Math.max(mScaleArcRadius, mProgressArcRadius) * 2 + mBallOverstepWidth * 2
                    + getPaddingLeft() + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(_Width, MeasureSpec.EXACTLY);
        }
        if (_HeightMode != MeasureSpec.EXACTLY) {
            int _MaxRadius = Math.max(mScaleArcRadius, mProgressArcRadius);
            int _Height =(int) (_MaxRadius + _MaxRadius * Math.sin(Math.toRadians(22.5))
                    + mBallOverstepWidth + _Ball.getHeight() / 2 + getPaddingTop() + getPaddingBottom());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(_Height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int _MaxRadius = Math.max(mProgressArcRadius, mScaleArcRadius);
        //移动原点到中心位置
        canvas.translate(_MaxRadius + mBallOverstepWidth, _MaxRadius + mBallOverstepWidth);
        drawScaleArc(canvas, 80, SCALE_COUNT);  //画刻度圆弧
        drawText(canvas);                       //画文字
    }



    /**
     * 画刻度圆弧
     * @param canvas Canvas
     * @param pAlpha 没有到达的透明度 0~255
     * @param pCount 点的个数
     */
    private void drawScaleArc(Canvas canvas, int pAlpha, int pCount) {


        Bitmap _Ball = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_sesame);

        canvas.save();
        canvas.rotate(-195f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mScaleArcWidth);
        float _ScaleArcL = mScaleArcRadius - mScaleArcWidth / 2.0f;
        RectF _ScaleArcRectF = new RectF(-_ScaleArcL, -_ScaleArcL, _ScaleArcL, _ScaleArcL);
        //画完每个刻度所要旋转的度数
        float _DialSpacing = (TOTAL_ANGLE - pCount) / (pCount - 1) + 1;
        float _TargetAngle = getTargetAngle(mCreditScore);
        float _CurrentAngle = 0;
        boolean _SetAlpha = false;
        for (int i = 0; i < pCount; i++) {

            if (_CurrentAngle > _TargetAngle && !_SetAlpha) {
                //设置未达到的点的透明度
                mPaint.setAlpha(pAlpha);

                _SetAlpha = true;
            }
            canvas.drawBitmap(_Ball, _ScaleArcL - _Ball.getHeight() , -(_Ball.getWidth() ), mPaint);

            canvas.rotate(_DialSpacing);
            _CurrentAngle += _DialSpacing;
        }
        //恢复透明度
        mPaint.setAlpha(255);
        canvas.restore();
    }


    /**
     * 画文字
     * @param canvas Canvas
     */
    private void drawText(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.colorWhite));
        //信用分数
        mPaint.setTextSize(mCreditScoreTextSize);
        String _CreditScore = String.valueOf(mCreditScore);
        float _Width = mPaint.measureText(_CreditScore);
        Rect _Rect = new Rect();
        mPaint.getTextBounds(_CreditScore, 0, _CreditScore.length(), _Rect);
        float _Y = 0;
        canvas.drawText(_CreditScore, -(_Width / 2.0f), _Y, mPaint);
        //信用级别
        String _CreditLevel = getCreditLevel(mCreditScore);

        //BETA
        _Y = _Y - _Rect.height() - mTextSpacing;
        mPaint.setTextSize(mBetaTextSize);
        mPaint.setAlpha(150);
        _Width = mPaint.measureText(BETA);
        canvas.drawText(BETA, -(_Width / 2.0f), _Y, mPaint);
        //评估时间
        String _EvaluationTime = _CreditLevel;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.colorWhite));
        _Width = mPaint.measureText(_EvaluationTime);
        mPaint.getTextBounds(_CreditLevel, 0, _CreditLevel.length(), _Rect);
        mPaint.setTextSize(mEvaluationTimeTextSize);
        _Y = mTextSpacing + _Rect.height();
        canvas.drawText(_EvaluationTime, -(_Width / 2.0f), _Y, mPaint);
    }

    /**
     * dp转px
     * @param pContext Context
     * @param pDpVal dp值
     * @return px值
     */
    private static int dp2px(Context pContext, int pDpVal) {
        float _Scale = pContext.getResources().getDisplayMetrics().density;
        return (int)(pDpVal * _Scale + 0.5f * (pDpVal >= 0 ? 1 : -1));
    }

    /**
     * 根据信用分数计算出目标角度
     * @param pCreditScore 信用分数
     * @return 目标角度
     */
    private float getTargetAngle(float pCreditScore) {
        if (pCreditScore > 700) {
            return 180 + (pCreditScore - 700) * 0.18f;
        } else if (pCreditScore > 550) {
            return 45 + (pCreditScore - 550) * 0.9f;
        } else {
            return (pCreditScore - 350) * 0.225f;
        }
    }

    /**
     * 根据信用分数获取信用级别
     * @param pCreditScore 信用分数
     * @return 信用级别
     */
    private String getCreditLevel(int pCreditScore) {
        if (pCreditScore >= 350 && pCreditScore < 550) {
            return CREDIT_LEVEL[0];
        } else if (pCreditScore >= 550 && pCreditScore < 600) {
            return CREDIT_LEVEL[1];
        } else if (pCreditScore >= 600 && pCreditScore < 650) {
            return CREDIT_LEVEL[2];
        } else if (pCreditScore >= 650 && pCreditScore < 700) {
            return CREDIT_LEVEL[3];
        } else if (pCreditScore >= 700 && pCreditScore <= 950){
            return CREDIT_LEVEL[4];
        } else {
            return CREDIT_LEVEL[0];
        }
    }
    public void setScore(int score)
    {
        mCreditScore = score;
        invalidate();
    }



}
