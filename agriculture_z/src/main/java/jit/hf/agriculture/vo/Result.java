package jit.hf.agriculture.vo;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午11:30 18-5-9
 **/
public class Result {
    private boolean tr1;
    private boolean tr2;
    private boolean tr3;
    private boolean tr4;
    private boolean tr5;
    private String score;

    public boolean isTr1() {
        return tr1;
    }

    public void setTr1(boolean tr1) {
        this.tr1 = tr1;
    }

    public boolean isTr2() {
        return tr2;
    }

    public void setTr2(boolean tr2) {
        this.tr2 = tr2;
    }

    public boolean isTr3() {
        return tr3;
    }

    public void setTr3(boolean tr3) {
        this.tr3 = tr3;
    }

    public boolean isTr4() {
        return tr4;
    }

    public void setTr4(boolean tr4) {
        this.tr4 = tr4;
    }

    public boolean isTr5() {
        return tr5;
    }

    public void setTr5(boolean tr5) {
        this.tr5 = tr5;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Result(boolean tr1, boolean tr2, boolean tr3, boolean tr4, boolean tr5, String score) {
        this.tr1=tr1;
        this.tr2=tr2;
        this.tr3=tr3;
        this.tr4=tr4;
        this.tr5=tr5;
        this.score=score;
    }

}
