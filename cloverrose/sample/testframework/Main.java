package cloverrose.sample.testframework;

import java.util.ArrayList;
import java.util.List;

import cloverrose.testframework.TestFramework;

public class Main {
	public static void main(String[] args) {

		//*.classファイルが入っているルートディレクトリの相対パス
		String binDir="bin";
	    //テストクラスが入っているパッケージを設定
        List<String> ps=new ArrayList<String>(){
        private static final long serialVersionUID = 1L;
          {
              add("cloverrose.sample.testframework.concrete");    
          }
        };
		TestFramework testFm=new TestFramework();
		testFm.set_binDir_packagePaths(binDir,ps);

		//テストを開始　結果はtrueになるはず
		boolean ret=testFm.test();
		System.out.println(ret);
	}

}
