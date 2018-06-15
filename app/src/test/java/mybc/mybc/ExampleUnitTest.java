package mybc.mybc;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void chaiGuanJun() throws Exception {
        String[] qiuDui={"巴西","德国","西班牙","法国","阿根廷","比利时","葡萄牙","英格兰","乌拉圭"};
        Random win = new Random();
        System.out.println("2018俄罗斯世界杯冠军："+qiuDui[win.nextInt(9)]);
    }
    @Test
    public void afd() throws Exception {
        System.out.println(convertStringToHex("123"));
        System.out.println(convertStringToHex("abd"));
        System.out.println(convertStringToHex("123abc"));
        System.out.println(convertStringToHex("艾丝凡123abc"));
        System.out.println(convertStringToHex("AT+LED=1") );
        System.out.println(toBytes(convertStringToHex("AT+LED=1").toUpperCase()));
        System.out.println(convertStringToHex("AT+LED=1").toUpperCase().getBytes());

        System.out.println(bytesToHexFun1(new byte[]{0x41,0x54,0x4C,0x45,0x44,0x3D,0x31}));
        System.out.println(new String(bytesToHexFun1(new byte[]{0x41,0x54,0x4C,0x45,0x44,0x3D,0x31})));

        String str="ATLED=1";
        System.out.println(convertStringToHex(str));
        System.out.println(hexStringToBytes(convertStringToHex(str)));
        System.out.println(hexStringToBytes(convertStringToHex(str)));
        System.out.println(new byte[]{0x41,0x54,0x4C,0x45,0x44,0x3D,0x31});
        System.out.println(new byte[]{0x41,0x54,0x2b,0x4c,0x45,0x44,0x3d,0x31});

        System.out.println(bytesToHexString(new byte[]{0x41,0x54,0x4C,0x45,0x44,0x3D,0x31}));
        System.out.println();
        System.out.println(hexStringToBytes("0x61,0x73,0x66,0x64,0xB0,0xB5,0xCA,0xD2,0xB7,0xEA,0xB5,0xC6"));
//        new byte[]{0x61,0x73,0x66,0x64,0xB0,0xB5,0xCA,0xD2,0xB7,0xEA,0xB5,0xC6};
        System.out.println(hexStringToBytes("61 73 66 64 B0 B5 CA D2 B7 EA B5 C6"));
        System.out.println(Hex.hexStr2Str("61 73 66 64 B0 B5 CA D2 B7 EA B5 C6"));
        System.out.println(Hex.str2HexStr("asfd暗室逢灯"));
        System.out.println(toChineseHex("asfd暗室逢灯"));
        System.out.println("asfd暗室逢灯".getBytes("GB2312"));
        System.out.println(Hex.byte2HexStr("asfd暗室逢灯".getBytes("GB2312")));
        System.out.println(Hex.hexStr2Str(Hex.str2HexStr("asfd暗室逢灯")));
        System.out.println(Hex.hexStr2Str(Hex.byte2HexStr("asfd暗室逢灯".getBytes("GB2312"))));


        System.out.println(toStringHex2("6173666466975ba49022706f"));
        System.out.println(toStringHex2("61736664e69a97e5aea4e980a2e781af"));
        System.out.println(convertStringToHex("asfd暗室逢灯"));
    }

    private   String hexString = "0123456789abcdef";
    public   String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
//将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
}
    public static String toStringHex2(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
    public static String toChineseHex(String s)

    {

        String ss = s;

        byte[] bt = ss.getBytes();

        String s1 = "";

        for (int i = 0; i < bt.length; i++)

        {

            String tempStr = Integer.toHexString(bt[i]);

            if (tempStr.length() > 2)

                tempStr = tempStr.substring(tempStr.length() - 2);

            s1 = s1 + tempStr + " ";

        }

        return s1.toUpperCase();

    }
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    public String convertStringToHex(String str){
//把字符串转换成char数组
        char[] chars = str.toCharArray();
//新建一个字符串缓存类
        StringBuffer hex = new StringBuffer();
//循环每一个char
        for(int i = 0; i < chars.length; i++){
//把每一个char都转换成16进制的，然后添加到字符串缓存对象中
            hex.append(Integer.toHexString((int)chars[i]));
        }
//最后返回字符串就是16进制的字符串
        return hex.toString();
    }

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * 方法一：
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexFun1(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for(byte b : bytes) { // 使用除与取余进行转换
            if(b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }
            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }
        return new String(buf);
    }
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }
    public   byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    public byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }



}