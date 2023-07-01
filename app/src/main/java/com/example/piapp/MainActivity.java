package com.example.piapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {
    Button scann_btn;
    Button pasar_turno;
    TextView presupuesto;
    TextView turno;
    Integer presupuesto_personajes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        scann_btn = findViewById(R.id.myButton);
        presupuesto = findViewById(R.id.presupuesto);
        pasar_turno = findViewById(R.id.pasar_turno);
        presupuesto_personajes = 0;
        turno = findViewById(R.id.turno);
        turno.setText("1");
        showOptionsDialog();

        scann_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(MainActivity.this)
                        .setOrientationLocked(true)
                        .setPrompt("Scan a QR Code")
                        .setBarcodeImageEnabled(true)
                        .initiateScan();
            }
        });

        pasar_turno.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int currentTurno = Integer.parseInt((String) turno.getText());
                if(currentTurno  == 6){
                    showEndGameModal();
                }
                else {
                    turno.setText(String.valueOf(currentTurno + 1));
                    presupuesto.setText(String.valueOf(Integer.parseInt((String) presupuesto.getText()) - presupuesto_personajes));
                }
            }
        });
    }
    private void showEndGameModal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fin de juego");
        builder.setMessage("¡El juego ha terminado!");
        builder.setPositiveButton("Aceptar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        presupuesto.setText("0");
    }
    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione su carta inicial")
                .setItems(new CharSequence[]{"Opción 1", "Opción 2", "Opción 3"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acciones correspondientes a cada opción seleccionada
                        switch (which) {
                            case 0:
                                // Acción para la opción 1
                                presupuesto.setText("5000");
                                break;
                            case 1:
                                // Acción para la opción 2
                                presupuesto.setText("2000");
                                break;
                            case 2:
                                presupuesto.setText("3000");
                                // Acción para la opción 3
                                break;
                        }

                        // Habilitar el botón positivo después de seleccionar una opción
                        //positiveButton.setEnabled(true);
                    }
                })
                .setCancelable(false)
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String[] contents = result.getContents().split(",");
            if(contents[0].equals("Personaje")){
                System.out.println(contents[0]);
                System.out.println(contents[1]);
                System.out.println(contents[2]);
                presupuesto_personajes += Integer.parseInt(contents[2]);
            }
            TableLayout table = new TableLayout(this);
            table.setLayoutParams(new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1f)); // Peso de 1 y ancho 0 para distribución equitativa
            for (String value : contents){
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                row.setBackgroundResource(R.drawable.table_cell_order);

                TextView textView = new TextView(this);
                textView.setText(value);
                textView.setPadding(8, 8, 8, 20); // Agregar padding de 8dp en todos los lados
                row.addView(textView);
                table.addView(row);
            }
            table.setPadding(10,10,10,10);
            presupuesto.setText(String.valueOf(Integer.parseInt((String) presupuesto.getText()) - Integer.parseInt(contents[2])));
            LinearLayout table_container = findViewById(R.id.table_container);
            table_container.addView(table);
        } else{
            Log.d("Main activity", "IntentResult is null");
            super.onActivityResult(requestCode,resultCode,data);
        }

    }
}
