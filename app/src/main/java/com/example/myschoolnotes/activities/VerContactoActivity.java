package com.example.myschoolnotes.activities;

import static com.example.myschoolnotes.activities.ContactosActivity.contactoSelected;
import static com.example.myschoolnotes.activities.LoginActivity.usuarioAct;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myschoolnotes.R;
import com.example.myschoolnotes.entities.Contacto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerContactoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ver_contacto);
        EditText nombreET = findViewById(R.id.nombreET);
        EditText numeroET = findViewById(R.id.numeroET);
        EditText emailET = findViewById(R.id.emailET);
        EditText direccionET = findViewById(R.id.direccionET);
        EditText aliasET = findViewById(R.id.aliasET);

        Button borrarBTN = findViewById(R.id.borrarBTN);
        Button editBTN = findViewById(R.id.editBTN);
        Button callBTN = findViewById(R.id.callBTN);
        Button realcallBTN = findViewById(R.id.realcallBTN);

        nombreET.setText(contactoSelected.getNombre());
        aliasET.setText(contactoSelected.getAlias());
        numeroET.setText(contactoSelected.getTelefono());
        emailET.setText(contactoSelected.getMail());
        direccionET.setText(contactoSelected.getDireccion());
        DatabaseReference uref = FirebaseDatabase.getInstance().getReference("agenda");
        DatabaseReference contactReference = uref.child("agendaUser"+usuarioAct.getUid());

        Query query = contactReference.orderByChild("telefono").equalTo(contactoSelected.getTelefono());
        borrarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String clave = ds.getKey();
                            contactReference.child(clave).removeValue();
                            Toast.makeText(VerContactoActivity.this, "Eliminado.", Toast.LENGTH_SHORT).show();

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                Intent intent = new Intent(VerContactoActivity.this, ContactsMenuActivity.class);
                startActivity(intent);
        }
    });

        editBTN.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view){

        Contacto c = new Contacto();
        String erroresContact = "Revise: \n";
        String nombre = String.valueOf(nombreET.getText());
        String alias = String.valueOf(aliasET.getText());
        String direccion = String.valueOf(direccionET.getText());
        String mail = String.valueOf(emailET.getText());
        String telefono = String.valueOf(numeroET.getText());

        Pattern stringRegex = Pattern.compile("^[A-Za-z]\\w{2,29}$");
        Matcher matcherNombre = stringRegex.matcher(nombre);
        Matcher matcherAlias = stringRegex.matcher(alias);

        Pattern direccionRegex = Pattern.compile("^[a-zA-Z0-9\\s]*$");
        Matcher matcherDireccion = direccionRegex.matcher(direccion);

        Pattern mailRegex = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcherMail = mailRegex.matcher(mail);

        Pattern movilRegex = Pattern.compile("^\\d{9}$");
        Matcher matcherPhone = movilRegex.matcher(telefono);

        if (matcherNombre.matches()) {
            c.setNombre(nombre);
        } else {
            erroresContact += "El nombre \n";
            nombreET.setText("");
        }

        if (matcherAlias.matches()) {
            c.setAlias(alias);
        } else {
            erroresContact += "El alias \n";
            aliasET.setText("");
        }

        if (matcherDireccion.matches()) {
            c.setDireccion(direccion);
        } else {
            erroresContact += "El domicilio \n";
            direccionET.setText("");
        }


        if (matcherMail.matches()) {
            c.setMail(mail);
        } else {
            erroresContact += "El formato del eMail \n";
            emailET.setText("");
        }


        if (matcherPhone.matches()) {
            c.setTelefono(telefono);
        } else {
            erroresContact += "El teléfono \n";
            numeroET.setText("");
        }

        if (!erroresContact.equals("Revise: \n")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(VerContactoActivity.this);
            builder.setMessage(erroresContact)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            builder.show();
            erroresContact = "Revise: \n";
        } else {

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean numeRep = false;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Contacto cAux = ds.getValue(Contacto.class);
                        if (cAux.getTelefono().equals(c.getTelefono())) {
                            if(cAux.getTelefono().equals(contactoSelected.getTelefono())){
                                numeRep = false;
                                break;

                            }else {
                                numeRep = true;
                                break;
                            }
                        }
                    }
                    if (numeRep) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(VerContactoActivity.this);
                        builder.setMessage("El número ya está registrado en su agenda")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        builder.show();
                    } else {
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String clave = ds.getKey();
                                    contactReference.child(clave).child("direccion").setValue(direccion);
                                    contactReference.child(clave).child("mail").setValue(mail);
                                    contactReference.child(clave).child("alias").setValue(alias);
                                    contactReference.child(clave).child("nombre").setValue(nombre);
                                    contactReference.child(clave).child("telefono").setValue(telefono);
                                    Toast.makeText(VerContactoActivity.this, "Actualizado.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        Intent intent = new Intent(VerContactoActivity.this, ContactsMenuActivity.class);
                        startActivity(intent);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });

        }
    }
    });
        callBTN.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numeroET.getText()));
        startActivity(i);
    }
    });

        /*En función del dispositivo la llamada puede fallar; en caso de que falle se podrá solucionar accediendo a los ajustes del dispositivo
          > aplicaciones > elegir MySchoolNotes y otrogarle los permisos de teléfono y/o llamadas
        * */
        realcallBTN.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view){
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numeroET.getText()));
        startActivity(i);
    }
    });

}

}