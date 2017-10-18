package com.example.alberto.pvideop.ImagenVideo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import com.example.alberto.pvideop.Reproductor;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private int i = 0,videoWidth, videoHeight,contador = 0;
    private int TIEMPO = 100;
    private GLES20 gl;
    private Context contexto;
    private boolean existe=true;
    private BroadcastReceiver myBroadcastReceiver;
    private float vColor =0.50f;
    public static boolean pausa = false;
    public static int tam_Img = 200;
    public static  String Caricatura = "caricatura";
    public static  String Futbol = "futbol";
    public static  String Documental = "documental";
    public static  String Guerra = "guerra";
    public static  String Noticia = "noticia";
    public static  String Novela = "novela";
    public static String RESULTADO = "";


    public MyRenderer() {
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);

        Matrix.setIdentityM(mSTMatrix, 0);
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.rotateM(mMMatrix, 0, 0, 0, 1, 0);
        Matrix.translateM(mMMatrix,0,-1,1,0);
    }

    public static void setPausa(boolean pause) {
        pausa = pause;
    }

    public static boolean isPausa() {
        return pausa;
    }

    public void setContext(Context context) {
        this.contexto = context;
    }


    public void onDrawFrame(GL10 glUnused) {
        synchronized(this) {
            if (updateSurface) {
                mSurface.updateTexImage();

                mSurface.getTransformMatrix(mSTMatrix);
                updateSurface = false;
            }
        }


        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20 gl = null;
       /* ((Activity) contexto).runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(contexto, R.style.MyDialogTheme);
                builder.setTitle("Vídeo inapropiado");
                builder.setIcon(R.drawable.icono);
             /*   View viewInflated = tLayoutInflater().inflate(R.layout.dialogo_opciones, null);
                final EditText claveActual = (EditText) viewInflated.findViewById(R.id.claveActual);
                final EditText claveNueva = (EditText) viewInflated.findViewById(R.id.claveNueval);
                builder.setView(viewInflated);
*/
  /*              builder.setMessage("Ingrese la contraseña para poder ver el video");
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Log.i("totos", "holis");
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                if(pausa)
                builder.show();

            }
        });*/

        gl.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        gl.glUseProgram(mProgram);

        //  checkGlError("glUseProgram");
        int color  = GLES20.glGetUniformLocation(mProgram,"aux");
        int aux= 0;
        if(pausa)
            vColor=1.0f;
        else
            vColor=0.0f;

      //  Log.e("color",""+vColor);

        GLES20.glUniform1f(color, vColor);

        gl.glActiveTexture(GLES20.GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);

        mVertices.position(VERTICES_DATA_POS_OFFSET);
        gl.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                VERTICES_DATA_STRIDE_BYTES, mVertices);
//        checkGlError("glVertexAttribPointer maPosition");
        gl.glEnableVertexAttribArray(maPositionHandle);
        //      checkGlError("glEnableVertexAttribArray maPositionHandle");

        mVertices.position(VERTICES_DATA_UV_OFFSET);
        gl.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false,
                VERTICES_DATA_STRIDE_BYTES, mVertices);
        //    checkGlError("glVertexAttribPointer maTextureHandle");
        gl.glEnableVertexAttribArray(maTextureHandle);
        //  checkGlError("glEnableVertexAttribArray maTextureHandle");

        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        gl.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        gl.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        gl.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

      Intent msgIntent  = new Intent(contexto, MiIntentService.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        contexto.startService(msgIntent);
        myBroadcastReceiver = new MyBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter(MiIntentService.ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        contexto.registerReceiver(myBroadcastReceiver, intentFilter);


        if(contador==TIEMPO) {
            Bitmap grafica = createBitmapFromGLSurface(0, 0, videoWidth, videoHeight, gl);
            saveToInternalStorage(grafica);
            contador = 0;
        }
        contador++;

    }


    public static void setTam_Img(int tam_Img) {
        MyRenderer.tam_Img = tam_Img;
    }

    public static int getTam_Img() {
        return tam_Img;
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int genero, cont;
            genero = (int) intent.getExtras().get(MiIntentService.GENERO);
            cont = (int) intent.getExtras().get(MiIntentService.TIME);
            TIEMPO= cont* 100;
            SharedPreferences settings = contexto.getSharedPreferences("mysettings", 0);
            final SharedPreferences.Editor editor = settings.edit();
/*
            Log.e("result check",""+settings.getBoolean("caricatura",false)+settings.getBoolean("documental",false)
                    +settings.getBoolean("futbol",false)+settings.getBoolean("guerra",false)
                    +settings.getBoolean("noticia",false)+settings.getBoolean("novela",false));*/
            Log.e("inapopiado",""+i);
            if(existe)
            switch (genero) {
                case 1:
                    Log.i("genero", Caricatura);
                    RESULTADO = Caricatura;
                    if(!settings.getBoolean("caricatura",false))
                        i++;
                    break;
                case 2:
                    Log.i("genero",Documental);
                    RESULTADO = Documental;
                    if(!settings.getBoolean("documental",false))
                        i++;
                    break;
                case 3:
                    Log.i("genero", Futbol);
                    RESULTADO = Futbol;
                    if(!settings.getBoolean("futbol",false))
                        i++;
                    break;
                case 4:
                    Log.i("genero",Guerra);
                    RESULTADO = Guerra;
                    if(!settings.getBoolean("guerra",false))
                        i++;
                    break;
                case 5:
                    Log.i("genero",Noticia);
                    RESULTADO = Noticia;
                    if(!settings.getBoolean("noticia",false))
                        i++;
                    break;
                case 6:
                    Log.i("genero",Novela);
                    RESULTADO = Novela;
                    if(!settings.getBoolean("novela",false))
                        i++;
                    break;
            };

            if(i==tam_Img) {
                i=0;
                tam_Img=tam_Img*2;
                pausa=true;
                Reproductor.mMediaPlayer.pause();

/*
                View viewInflated = getLayoutInflater().inflate(R.layout.dialogo_opciones, null);
                final EditText claveActual = (EditText) viewInflated.findViewById(R.id.claveActual);
                final EditText claveNueva = (EditText) viewInflated.findViewById(R.id.claveNueval);
                builder.setView(viewInflated);
*/
           /*     builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
              //          clave_actual = claveActual.getText().toString();
                //        clave_nueva = claveNueva.getText().toString();
                  //      Log.i("totos",clave_actual+" "+clave_nueva);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
        //        });

       //         builder.show();
                                                       }
                                                   }
                );
          //      Reproductor.dialogo(contexto);
            //    toast("se detendrá el video");
         /*       AlertDialog.Builder builder = new AlertDialog.Builder(contexto, R.style.MyDialogTheme);
                builder.setTitle("Cambiar Clave");
                builder.setIcon(R.drawable.icono);
/*
                View viewInflated = getLayoutInflater().inflate(R.layout.dialogo_opciones, null);
                final EditText claveActual = (EditText) viewInflated.findViewById(R.id.claveActual);
                final EditText claveNueva = (EditText) viewInflated.findViewById(R.id.claveNueval);
                builder.setView(viewInflated);
*/
           /*     builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
              //          clave_actual = claveActual.getText().toString();
                //        clave_nueva = claveNueva.getText().toString();
                  //      Log.i("totos",clave_actual+" "+clave_nueva);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
        //        });

       //         builder.show();
*/
            }
        }
    }

    public void toast(String  text){
        Toast.makeText(contexto.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }



    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        this.videoWidth = width;
        this.videoHeight = height;
        GLES20.glViewport(0, 0, width, height);
        mRatio = (float) width / height;
        Matrix.frustumM(mProjMatrix, 0, -mRatio, mRatio, 0, 1, 3, 7);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.

            /* Set up alpha blending and an Android background color */
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(0.643f, 0.776f, 0.223f, 1.0f);

            /* Set up shaders and handles to their variables */
        mProgram = createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }


        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

      /*  muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }*/

        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        checkGlError("glGetUniformLocation uSTMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        checkGlError("glGetUniformLocation uCRatio");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uCRatio");
        }

            /*
             * Create our texture. This has to be done each time the
             * surface is created.
             */

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        checkGlError("glBindTexture mTextureID");

        // Can't do mipmapping with camera source
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        // Clamp to edge is the only option
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        checkGlError("glTexParameteri mTextureID");

        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);

        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 4f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        synchronized(this) {
            updateSurface = false;
        }
    }

    synchronized public void onFrameAvailable(SurfaceTexture surface) {
            /* For simplicity, SurfaceTexture calls here when it has new
             * data available.  Call may come in from some random thread,
             * so let's be safe and use synchronize. No OpenGL calls can be done here.
             */
        updateSurface = true;
        //Log.v(TAG, "onFrameAvailable " + surface.getTimestamp());
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int VERTICES_DATA_POS_OFFSET = 0;
    private static final int VERTICES_DATA_UV_OFFSET = 3;
    private final float[] mVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 0.f,
            1.0f, -1.0f, 0, 1.f, 0.f,
            -1.0f,  1.0f, 0, 0.f, 1.f,
            1.0f,   1.0f, 0, 1.f, 1.f,
    };

    private FloatBuffer mVertices;

    private final String mVertexShader =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    private final String mFragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform float aux;"+
                    "void main() {\n" +
                    "   vec4 color = texture2D(sTexture, vTextureCoord);\n" +
                    "   gl_FragColor = color;" +

                    "}\n";

    private float[] mMVPMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mMMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] mSTMatrix = new float[16];

    private int mProgram;
    private int mTextureID;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;

    private float mRatio = 1.0f;
    private SurfaceTexture mSurface;
    private boolean updateSurface = false;

    private static final String TAG = "MyRenderer";

    // Magic key
    private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    public SurfaceTexture getSurfaceTexture() {
        return mSurface;
    }

    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GLES20 gl)
            throws OutOfMemoryError {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }



    private void saveToInternalStorage(Bitmap bitmapImage){
//        String directory = "/storage/emulated/0/DCIM/.thumbnails/";
        String directory = "/storage/emulated/0/DCIM/";
        File mypath=new File(directory,"profile.png");
        bitmapImage = scaleDown(bitmapImage, 32, true);


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, 32,
                32, filter);
        return newBitmap;
    }



}
