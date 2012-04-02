package cloverrose.testframework;

public abstract class Tester {
	/**
     * mainWorkに渡す文字列を設定
     * @return
     */
	protected abstract String makeInputString();
	/**
	 * 期待されるmainWorkが返す文字列を設定
	 * @return
	 */
	protected abstract String makeOutputString();
	/**
	 * 入力文字列に対して行う処理を記述
	 * そして最後に結果を文字列として返す
	 * @param input
	 * @return
	 */
    protected abstract String mainWork(String input);

	/**
	 * 
	 * @return
	 */
	final boolean test(){
		String output=mainWork(this.makeInputString());
		if(!makeOutputString().equals(output)){//テストで誤りを検出
			String cn=this.getClass().getName();
			System.err.println("[cloverrose.testframework] found mismatch at class ["+cn+"]");
	     	return false;
	    }
		return true;
	}
}
