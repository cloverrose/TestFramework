package cloverrose.sample.testframework.concrete;

import cloverrose.testframework.Tester;

/**
 * "Japan"と入力して文字数5を得て、その結果を文字列"5"として返し
 * 期待した"5"とあってるか検査する簡単なクラス
 */
public class FiveTester extends Tester{
	@Override
	protected String makeInputString() {
		return "Japan";
	}

	@Override
	protected String makeOutputString() {
		return "5";
	}
	
	@Override
	protected String mainWork(String input) {
        int count=input.length();
        System.out.println("call");
        return ""+count;
    }
}
