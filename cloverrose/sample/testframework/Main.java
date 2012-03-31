package cloverrose.sample.testframework;

import java.util.ArrayList;
import java.util.List;

import cloverrose.testframework.TestFramework;

public class Main {
	public static void main(String[] args) {

	    //テストクラスが入っているパッケージを設定
        List<String> ps=new ArrayList<String>(){
        private static final long serialVersionUID = 1L;
          {
              add("cloverrose.sample.testframework.concrete");    
          }
        };
		TestFramework.getInstance().setPackagePaths(ps);

		//テストを開始　結果はtrueになるはず
		boolean ret=TestFramework.getInstance().test();
		System.out.println(ret);
	}

}
