package jp.j765375.test.encoding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.junit.Before;
import org.junit.Test;

public class CharEncodingTest {

	private Charset utf16;
	private Charset utf8;
	private Charset ms932;
	private Charset sjis;
	private CharsetEncoder utf16Enc;
	private CharsetEncoder utf8Enc;
	private CharsetEncoder ms932Enc;
	private CharsetEncoder sjisEnc;

	@Before
	public void setUp() {
		utf8 = Charset.forName("UTF-8");
		utf16 = Charset.forName("UTF-16");
		ms932 = Charset.forName("windows-31j");
		sjis = Charset.forName("Shift_JIS");
		utf8Enc = utf8.newEncoder();
		utf16Enc = utf16.newEncoder();
		ms932Enc = ms932.newEncoder();
		sjisEnc = sjis.newEncoder();
	}

	@Test
	public void combiningCharacter() {
		// 結合文字 か + 半濁点
		print("\u304B\u309A");
		// 結合文字 か + 濁点 （結合文字ではない普通の「が」に変換可能）
		print("\u304B\u309B");
	}

	@Test
	public void surrogatePair() {
		// サロゲートペア「U+29E15」𩸕
		// 文字の符号位置ではなく、UTF-16のコード値を指定する必要がある
		print("\uD867\uDE15");
	}

	@Test
	public void waveDash() {
		// 波ダッシュ
		// windows-31jでは変換できないが、Shift_JISでは変換できる。
		print("\u301C");
		// 全角チルダ
		// windows-31jでは変換できるが、Shift_JISでは変換できない。
		print("\uFF5E");
	}
	
	private void print(String s) {
		StringWriter result = new StringWriter();
		// printlnが使いたいのでPrintWriterを使う。
		PrintWriter w = new PrintWriter(result, true);
		try {
			w.println("============================");
			w.println(s);
			w.println(s.codePointAt(0));
			// 文字数情報
			w.println("length: " + s.length());
			w.println("codePointCount: " + s.codePointCount(0, s.length()));
			// 変換可能か？
			w.println("UTF-16変換可能      : " + utf16Enc.canEncode(s));
			w.println("UTF-8変換可能       : " + utf8Enc.canEncode(s));
			w.println("windows-31j変換可能 : " + ms932Enc.canEncode(s));
			w.println("Shift_JIS変換可能   : " + sjisEnc.canEncode(s));
			// バイト列
			if (utf16Enc.canEncode(s)) {
				// 先頭にFE FFが入るが、これはバイトオーダーマーク
				w.println("UTF-16バイト列      : "
						+ toHexString(s.getBytes("UTF-16")));
			}
			if (utf8Enc.canEncode(s)) {
				w.println("UTF-8バイト列       : "
						+ toHexString(s.getBytes("UTF-8")));
			}
			if (ms932Enc.canEncode(s)) {
				w.println("windows-31jバイト列 : "
						+ toHexString(s.getBytes("windows-31j")));
			}
			if (sjisEnc.canEncode(s)) {
				w.println("Shift_JISバイト列   : "
						+ toHexString(s.getBytes("Shift_JIS")));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		w.flush();
		result.flush();
		System.out.println(result.toString());
	}

	private String toHexString(byte[] byteArray) {

		// 結果を入れるためのバッファを用意
		StringBuilder sb = new StringBuilder();

		int i;
		for (byte b : byteArray) {

			// バイトを自然数に変換... するときは注意が必要
			// もし b が負数の場合を考慮して、0xFFとの論理積をとり下位8bitの値だけをみるように
			i = 0xFF & (int) b;

			// これを、16進数で表現
			String str = Integer.toHexString(i);

			// バッファに追加
			sb.append(str).append(" ");
		}

		return sb.toString();

	}
}
