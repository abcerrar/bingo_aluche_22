package com.example.bingo_aluche_2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bingo_aluche_2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class iniciar_sesion extends Fragment {

    private boolean login = true;
    private TextView boton_registrarse;
    private EditText nombre, email, contraseña, repetircontraseña;
    private Button iniciar_sesion;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String TAG = "iniciar_sesion.java";


    public iniciar_sesion() {
        // Required empty public constructor
    }

    public static iniciar_sesion newInstance(String param1, String param2) {
        iniciar_sesion fragment = new iniciar_sesion();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_iniciar_sesion, container, false);

        boton_registrarse = v.findViewById(R.id.login_registrarse);
        iniciar_sesion = v.findViewById(R.id.login_boton);
        nombre = v.findViewById(R.id.login_nombre);
        email = v.findViewById(R.id.login_email);
        contraseña = v.findViewById(R.id.login_contraseña);
        repetircontraseña = v.findViewById(R.id.login_repetircontraseña);

        boton_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login){
                    login = false;
                    iniciar_sesion.setText("Registrarse");
                    boton_registrarse.setText("Ya tengo cuenta");
                    nombre.setVisibility(View.VISIBLE);
                    repetircontraseña.setVisibility(View.VISIBLE);
                    nombre.requestFocus();
                }else{
                    login = true;
                    iniciar_sesion.setText("Iniciar sesión");
                    boton_registrarse.setText("No tengo cuenta");
                    nombre.setVisibility(View.INVISIBLE);
                    repetircontraseña.setVisibility(View.INVISIBLE);
                    email.requestFocus();
                }
            }
        });

        iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Algo hago", Toast.LENGTH_SHORT).show();
                String local_email = email.getText().toString();
                String pass = contraseña.getText().toString();

                if(local_email.equals("")) Toast.makeText(getContext(), "Debes introducir un email", Toast.LENGTH_SHORT).show();
                else if(pass.equals("")) Toast.makeText(getContext(), "Debes introducir una contraseña", Toast.LENGTH_SHORT).show();
                else if(login) iniciarSesion(local_email, pass);
                else{
                    String local_nombre = nombre.getText().toString();
                    String pass2 = repetircontraseña.getText().toString();

                    if(local_nombre.equals("")) Toast.makeText(getContext(), "Debes introducir un nombre", Toast.LENGTH_SHORT).show();
                    else if(!pass.equals(pass2)){
                        Toast.makeText(getContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                        contraseña.setText("");
                        repetircontraseña.setText("");
                    }
                    else registrarUsuario(local_email, pass);
                }

            }
        });

        return v;
    }
    public void registrarUsuario(String email, String pass){
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> usuario = new HashMap<>();
                            usuario.put("nombre", nombre.getText().toString());

                            db.collection("users").document(email).set(usuario)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error en addToUser", e);
                                        }
                                    });

                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Ese correo ya esta en uso o no tienes conexion.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    public void iniciarSesion(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Ese correo y esa contraseña no coinciden.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    public void updateUI(FirebaseUser account){
        if(account != null){
            Toast.makeText(getContext(),"You Signed In successfully",Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), MainActivity.class));
        }else {
            Toast.makeText(getContext(),"You Didnt signed in",Toast.LENGTH_LONG).show();
        }
    }
}