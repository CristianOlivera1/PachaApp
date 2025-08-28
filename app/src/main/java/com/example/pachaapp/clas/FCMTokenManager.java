package com.example.pachaapp.clas;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMTokenManager {
    private static final String TAG = "FCMTokenManager";
    private static final String PREFS_NAME = "fcm_prefs";
    private static final String TOKEN_KEY = "fcm_token";
    private static final String TOKEN_SENT_KEY = "fcm_token_sent";

    /**
     * Obtener el token FCM actual
     */
    public static void getToken(TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    callback.onFailure(task.getException());
                    return;
                }


                // Get new FCM registration token
                String token = task.getResult();
                Log.d(TAG, "FCM Registration Token: " + token);
                callback.onSuccess(token);
            });
    }

    /**
     * Guardar el token localmente
     */
    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .putString(TOKEN_KEY, token)
            .putBoolean(TOKEN_SENT_KEY, false)
            .apply();
        Log.d(TAG, "Token guardado localmente");
    }

    /**
     * Obtener el token guardado localmente
     */
    public static String getSavedToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, null);
    }

    /**
     * Verificar si el token ha sido enviado al servidor
     */
    public static boolean isTokenSent(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(TOKEN_SENT_KEY, false);
    }

    /**
     * Marcar el token como enviado al servidor
     */
    public static void markTokenAsSent(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(TOKEN_SENT_KEY, true).apply();
    }

    /**
     * Enviar el token al servidor
     */
    public static void sendTokenToServer(Context context, String token) {
        // Obtener el userId de las preferencias
        SharedPreferences userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userId = userPrefs.getString("user_id", null);
        
        if (userId == null) {
            Log.w(TAG, "No se puede enviar token: usuario no logueado");
            return;
        }

        // Crear request para actualizar token
        FCMTokenRequest request = new FCMTokenRequest();
        request.setIdUsuario(userId);
        request.setFcmToken(token);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<String>> call = apiService.updateFCMToken(request);

        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Log.d(TAG, "Token enviado al servidor exitosamente");
                        markTokenAsSent(context);
                    } else {
                        Log.e(TAG, "Error al enviar token: " + apiResponse.getFirstMessage());
                    }
                } else {
                    Log.e(TAG, "Error en respuesta del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al enviar token: " + t.getMessage());
            }
        });
    }

    /**
     * Inicializar FCM para un usuario logueado
     */
    public static void initializeForUser(Context context) {
        getToken(new TokenCallback() {
            @Override
            public void onSuccess(String token) {
                saveToken(context, token);
                if (!isTokenSent(context)) {
                    sendTokenToServer(context, token);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error al obtener token FCM", e);
            }
        });
    }

    /**
     * Callback interface para el token
     */
    public interface TokenCallback {
        void onSuccess(String token);
        void onFailure(Exception e);
    }
}
