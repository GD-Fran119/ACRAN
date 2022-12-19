package com.example.ism;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myExpandableListAdapter.MyExpandableListAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConsultaDiaResultActivity extends AppCompatActivity {
    private String jsonString, nombreAula;
    private ArrayList<String> groupList;
    private Map<String, ArrayList<String>> children;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private JSONArray json;
    private Context contexto = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_reservas);

        Intent intencion = getIntent();

        jsonString = intencion.getStringExtra("message");
        try{
            json = new JSONArray(jsonString);
        }catch (Exception e){
            return;
        }

        expandableListView = findViewById(R.id.elvClases);
        createListView();
    }

    private void createListView(){
        assert json != null;

        //Crea el ArrayList de horas y los children
        createGroupListAndChildren();

        //Si no hay clases se avisa al usuario y no se crea la lista
        if(groupList.isEmpty()){
            Toast.makeText(contexto, "No existen clases para esta aula hoy", Toast.LENGTH_LONG).show();
            return;
        }

        setTitle(nombreAula);

        //Para establecer cómo se van a ver grupo e hijos
        //Usar siempre esto
        adapter = new MyExpandableListAdapter(this, groupList, children){
            @Override
            public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                String className = getGroup(i).toString();
                if(view == null){
                    LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.group_item, null);
                }
                TextView item = view.findViewById(R.id.clase);
                item.setTypeface(null, Typeface.BOLD);
                item.setText(className);
                return view;
            }

            @Override
            public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
                String datos = getChild(i, i1).toString();
                if (view == null){
                    LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.child_item, null);
                }
                TextView item = view.findViewById(R.id.datos);
                item.setText(datos);

                return view;
            }
        };

        //Se establece el adapter para la lista
        expandableListView.setAdapter(adapter);

        //Copiado tal cual
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1;
            @Override
            public void onGroupExpand(int i) {
                if(lastExpandedPosition != -1 && i != lastExpandedPosition){
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = i;
            }
        });
        expandableListView.setOnChildClickListener((expandableListView, view, i, i1, l) -> {
            //Do nothing when selecting children
            return false;
        });

    }

    private void createGroupListAndChildren(){
        groupList = new ArrayList<String>();
        children = new HashMap<>();

        try {
            for (int i = 0; i < json.length(); ++i) {
                //Se crea el grupo
                String inicio = json.getJSONObject(i).getString("hora_inicio");
                String fin = json.getJSONObject(i).getString("hora_fin");
                String key = inicio + " - " + fin;
                groupList.add(key);//Añade las horas

                String aula = json.getJSONObject(i).getString("aula");
                String edificio = json.getJSONObject(i).getString("edificio");
                //Se crea el título del activity
                if(nombreAula == null || nombreAula.equals(""))
                    nombreAula = edificio + " - " + aula;

                //Se crean los hijos
                String grado = json.getJSONObject(i).getString("grado");
                String asignatura = json.getJSONObject(i).getString("asignatura");
                String value = "Asignatura: " + asignatura + "\nGrado: " + grado;

                ArrayList<String> hijos = new ArrayList<>();
                hijos.add(value);
                //Se insertan
                children.put(key, hijos);
            }
        }catch (Exception e){}

    }

    public void volver(View view){ onBackPressed(); }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, MainActivity.class));

        overridePendingTransition(R.transition.volver_transicion_entrada, R.transition.volver_transicion_salida);
    }
}