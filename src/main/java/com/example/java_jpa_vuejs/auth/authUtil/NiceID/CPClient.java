// Source code is decompiled from a .class file using FernFlower decompiler.
package com.example.java_jpa_vuejs.auth.authUtil.NiceID;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CPClient {
   private String a = "";
   private String b = "";
   private String c = "";
   private String d = "";
   private static byte[] e = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
   private static byte[] f = new byte[128];

   public CPClient() {
   }

   private static int a() {
      return Math.abs((new Random(System.currentTimeMillis())).nextInt());
   }

   private static byte[] a(byte[] var0) {
      boolean var1 = true;
      byte[] var3 = null;

      try {
         MessageDigest var2;
         (var2 = MessageDigest.getInstance("SHA-256")).reset();
         var2.update(var0);
         var3 = new byte[32];
         var3 = var2.digest();
      } catch (Exception var4) {
         var1 = false;
      }

      return var1 ? var3 : null;
   }

   private static byte[] a(byte[] var0, byte[] var1, byte[] var2) {
      boolean var3 = true;
      byte[] var5 = null;

      try {
         SecretKeySpec var7 = new SecretKeySpec(var0, "AES");
         Cipher var4;
         (var4 = Cipher.getInstance("AES/CBC/PKCS5Padding")).init(1, var7, new IvParameterSpec(var1));
         var5 = var4.doFinal(var2);
      } catch (Exception var6) {
         var3 = false;
      }

      return var3 ? var5 : null;
   }

   private static byte[] b(byte[] var0, byte[] var1, byte[] var2) {
      boolean var3 = true;
      byte[] var5 = null;

      try {
         SecretKeySpec var7 = new SecretKeySpec(var0, "AES");
         Cipher var4;
         (var4 = Cipher.getInstance("AES/CBC/PKCS5Padding")).init(2, var7, new IvParameterSpec(var1));
         var5 = var4.doFinal(var2);
      } catch (Exception var6) {
         var6.printStackTrace();
         var3 = false;
      }

      return var3 ? var5 : null;
   }

   private static int b(byte[] var0) {
      byte var3;
      try {
         if (var0 != null && var0.length >= 3) {
            Byte var1 = new Byte(var0[0]);
            new Byte(var0[1]);
            if (var1.compareTo((byte)2) == 0) {
               var3 = 0;
            } else {
               var3 = -7;
            }
         } else {
            var3 = -8;
         }
      } catch (Exception var2) {
         var3 = -8;
      }

      return var3;
   }

   private static String b() {
      String var2;
      try {
         var2 = InetAddress.getLocalHost().getHostAddress();
      } catch (Exception var3) {
         var2 = "UNKNOWN_ADDRESS";
      }

      int var1;
      if ((var1 = 16 - var2.length() - 1) > 0) {
         for(int var0 = 0; var0 < var1; ++var0) {
            var2 = var2 + " ";
         }
      }

      return var2.substring(0, 15);
   }

   public String getRequestNO() {
      return this.getRequestNO("");
   }

   public String getRequestNO(String var1) {
      StringBuffer var2 = new StringBuffer();
      Date var3 = new Date(System.currentTimeMillis());
      SimpleDateFormat var4 = new SimpleDateFormat("yyyyMMddHHmmssSSS");

      try {
         if (var1 != null && var1.trim().length() > 0) {
            var2.append(var1.trim());
            var2.append("_");
         }

         var2.append(var4.format(var3));
         var2.append((new Integer(a() % 100)).toString());
         var1 = var2.toString();
      } catch (Exception var5) {
         var1 = var4.format(var3);
      }

      return var1;
   }

   public int fnEncode(String var1, String var2, String var3) {
      this.d = "";
      if (var1.trim().length() != 0 && var1.trim().length() <= 127 && var2.trim().length() != 0) {
         byte var4 = 0;
         Date var5 = new Date(System.currentTimeMillis());
         String var8 = (new SimpleDateFormat("yyMMddHHmmss")).format(var5);
         var1 = var1.trim();
         String var10 = var2.trim();
         StringBuffer var13 = new StringBuffer();

         try {
            byte[] var7;
            int var20 = (var7 = var1.getBytes()).length;
            byte[] var6;
            (var6 = new byte[3])[0] = 2;
            var6[1] = 2;
            var6[2] = Integer.valueOf(var20).byteValue();
            int var16 = var20 + 3;
            var13.append(var8);
            var13.append("^");
            var13.append(b());
            var13.append("^");
            var13.append(var3);
            byte[] var9 = a(var13.toString().getBytes());
            var16 += 32;
            byte[] var22 = a((new Integer(a())).toString().getBytes());
            var16 += 32;
            byte[] var18 = a(var10.getBytes());
            byte[] var23 = new byte[16];
            byte[] var11 = new byte[16];

            int var15;
            for(var15 = 0; var15 < 16; ++var15) {
               var23[var15] = var18[var15];
               var11[var15] = var18[var15 + 16];
            }

            SecureRandom var17 = new SecureRandom();
            var18 = new byte[20];
            var17.nextBytes(var18);
            var18 = a(var18);
            if ((var23 = a(var23, var11, var18)) != null) {
               var16 += 48;
               var11 = new byte[16];
               byte[] var12 = new byte[16];

               for(var15 = 0; var15 < 16; ++var15) {
                  var11[var15] = var18[var15];
                  var12[var15] = var18[var15 + 16];
               }

               if ((var11 = a(var11, var12, var13.toString().getBytes())) != null) {
                  int var19 = var11.length;
                  var12 = new byte[var16 + var19];
                  var16 = 0;

                  for(var15 = 0; var15 < 3; ++var15) {
                     var12[var16] = var6[var15];
                     ++var16;
                  }

                  for(var15 = 0; var15 < var20; ++var15) {
                     var12[var16] = var7[var15];
                     ++var16;
                  }

                  for(var15 = 0; var15 < 32; ++var15) {
                     var12[var16] = var9[var15];
                     ++var16;
                  }

                  for(var15 = 0; var15 < 32; ++var15) {
                     var12[var16] = var22[var15];
                     ++var16;
                  }

                  for(var15 = 0; var15 < 48; ++var15) {
                     var12[var16] = var23[var15];
                     ++var16;
                  }

                  for(var15 = 0; var15 < var19; ++var15) {
                     var12[var16] = var11[var15];
                     ++var16;
                  }

                  byte[] var21 = var12;
                  String var10001;
                  if (var12.length == 0) {
                     var10001 = null;
                  } else {
                     var18 = new byte[(var12.length + 2) / 3 << 2];
                     var16 = 0;

                     for(var20 = 0; var16 < var21.length - 2; var16 += 3) {
                        var18[var20++] = e[var21[var16] >>> 2 & 63];
                        var18[var20++] = e[var21[var16 + 1] >>> 4 & 15 | var21[var16] << 4 & 63];
                        var18[var20++] = e[var21[var16 + 2] >>> 6 & 3 | var21[var16 + 1] << 2 & 63];
                        var18[var20++] = e[var21[var16 + 2] & 63];
                     }

                     if (var16 < var21.length) {
                        var18[var20++] = e[var21[var16] >>> 2 & 63];
                        if (var16 < var21.length - 1) {
                           var18[var20++] = e[var21[var16 + 1] >>> 4 & 15 | var21[var16] << 4 & 63];
                           var18[var20++] = e[var21[var16 + 1] << 2 & 63];
                        } else {
                           var18[var20++] = e[var21[var16] << 4 & 63];
                        }
                     }

                     while(var20 < var18.length) {
                        var18[var20] = 61;
                        ++var20;
                     }

                     var10001 = new String(var18, "euc-kr");
                  }

                  this.d = var10001;
               } else {
                  var4 = -2;
               }
            } else {
               var4 = -2;
            }
         } catch (Exception var14) {
            var14.printStackTrace();
            var4 = -2;
         }

         return var4;
      } else {
         return -9;
      }
   }

   public String getCipherData() {
      return this.d;
   }

   public int fnDecode(String var1, String var2, String var3) {
      this.c = "";
      this.a = "";
      this.b = "";
      if (var1.trim().length() != 0 && var1.trim().length() <= 127 && var2.trim().length() != 0) {
         byte var4 = 0;
         String var8 = var1.trim();
         String var9 = var2.trim();

         try {
            int var5;
            byte[] var6;
            int var12;
            int var13;
            byte[] var14;
            byte[] var10000;
            if (var3 != null && var3.length() != 0) {
               for(var5 = (var14 = var3.getBytes("euc-kr")).length; var14[var5 - 1] == 61; --var5) {
               }

               var6 = new byte[var5 - var14.length / 4];

               for(var12 = 0; var12 < var14.length; ++var12) {
                  var14[var12] = f[var14[var12]];
               }

               var12 = 0;

               for(var13 = 0; var13 < var6.length - 2; var13 += 3) {
                  var6[var13] = (byte)(var14[var12] << 2 & 255 | var14[var12 + 1] >>> 4 & 3);
                  var6[var13 + 1] = (byte)(var14[var12 + 1] << 4 & 255 | var14[var12 + 2] >>> 2 & 15);
                  var6[var13 + 2] = (byte)(var14[var12 + 2] << 6 & 255 | var14[var12 + 3] & 63);
                  var12 += 4;
               }

               if (var13 < var6.length) {
                  var6[var13] = (byte)(var14[var12] << 2 & 255 | var14[var12 + 1] >>> 4 & 3);
               }

               ++var13;
               if (var13 < var6.length) {
                  var6[var13] = (byte)(var14[var12 + 1] << 4 & 255 | var14[var12 + 2] >>> 2 & 15);
               }

               var10000 = var6;
            } else {
               var10000 = null;
            }

            byte[] var17 = var10000;
            if (var10000 != null && b(var17) == 0) {
               int var15 = var17.length;
               var12 = (new Byte(var17[2])).intValue();
               var13 = var12 + 3;
               if (var15 > var13 + 32 + 32 + 48) {
                  byte[] var7 = new byte[32];

                  for(var12 = 0; var12 < 32; ++var12) {
                     var7[var12] = var17[var13];
                     ++var13;
                  }

                  var6 = new byte[32];

                  for(var12 = 0; var12 < 32; ++var12) {
                     var6[var12] = var17[var13];
                     ++var13;
                  }

                  var6 = new byte[48];

                  for(var12 = 0; var12 < 48; ++var12) {
                     var6[var12] = var17[var13];
                     ++var13;
                  }

                  a((var8 + var9).getBytes());
                  byte[] var20 = a(a(var9.getBytes()));
                  byte[] var21 = new byte[16];
                  byte[] var10 = new byte[16];

                  for(var12 = 0; var12 < 16; ++var12) {
                     var21[var12] = var20[var12];
                     var10[var12] = var20[var12 + 16];
                  }

                  if ((var6 = b(var21, var10, var6)) != null) {
                     var20 = new byte[var15 -= var13];

                     for(var12 = 0; var12 < var15; ++var12) {
                        var20[var12] = var17[var13];
                        ++var13;
                     }

                     byte[] var16 = new byte[16];
                     var14 = new byte[16];

                     for(var12 = 0; var12 < 16; ++var12) {
                        var16[var12] = var6[var12];
                        var14[var12] = var6[var12 + 16];
                     }

                     if ((var16 = b(var16, var14, var20)) != null) {
                        byte[] var18 = a(var16);
                        var14 = var18;
                        var18 = var7;
                        boolean var19 = true;
                        int var22 = var7.length;
                        boolean var23;
                        if (var14.length != 32) {
                           var23 = false;
                        } else {
                           for(var5 = 0; var5 < 32; ++var5) {
                              if (var18[var5] != var14[var5]) {
                                 var19 = false;
                                 break;
                              }
                           }

                           var23 = var19;
                        }

                        if (var23) {
                           var1 = new String(var16, "euc-kr");
                           if (var16.length >= 29 && var1.charAt(12) == '^' && var1.charAt(28) == '^') {
                              this.a = var1.substring(0, 12);
                              this.b = var1.substring(13, 28);
                              this.c = var1.substring(29);
                           } else {
                              var4 = -6;
                           }
                        } else {
                           var4 = -5;
                        }
                     } else {
                        var4 = -4;
                     }
                  } else {
                     var4 = -6;
                  }
               } else {
                  var4 = -6;
               }
            } else {
               var4 = -6;
            }
         } catch (Exception var11) {
            var11.printStackTrace();
            var4 = -4;
         }

         return var4;
      } else {
         return -9;
      }
   }

   public String getPlainData() {
      return this.c;
   }

   public String getCipherDateTime() {
      return this.a;
   }

   public String getCipherIPAddress() {
      return this.b.trim();
   }

   public HashMap fnParse(String var1) {
      HashMap var2 = new HashMap();
      byte[] var3 = null;

      try {
         var3 = var1.getBytes("euc-kr");
      } catch (Exception var8) {
      }

      int var10 = var3.length;
      int var4 = 0;

      try {
         while(var4 < var10) {
            int var5;
            if ((var5 = a(var3, ':', var4)) < 0) {
               throw new Exception("[ERROR] missing #1:");
            }

            var4 = Integer.parseInt(a(var3, var4, var5));
            if (var5 >= var10) {
               throw new Exception("[ERROR] length error #1");
            }

            String var6 = a(var3, var5 + 1, var5 + var4 + 1);
            var4 = var5 + var4 + 1;
            if ((var5 = a(var3, ':', var4)) < 0) {
               throw new Exception("[ERROR] missing #2:");
            }

            var4 = Integer.parseInt(a(var3, var4, var5));
            if (var5 >= var10) {
               throw new Exception("[ERROR] length error #2");
            }

            String var7 = a(var3, var5 + 1, var5 + var4 + 1);
            var4 = var5 + var4 + 1;
            var2.put(var6, var7);
         }
      } catch (Exception var9) {
         var2.clear();
         var2 = null;
         var9.printStackTrace();
      }

      return var2;
   }

   private static int a(byte[] var0, char var1, int var2) {
        for (int i = var2; i < var0.length; i++) {
            if ((char) var0[i] == var1) {
                return i;
            }
        }
        return -1;
    }

    private static String a(byte[] var0, int var1, int var2) {
        return new String(Arrays.copyOfRange(var0, var1, var2), Charset.forName("euc-kr"));
    }
   static {
      for(int var0 = 0; var0 < e.length; ++var0) {
         f[e[var0]] = (byte)var0;
      }

   }
}
