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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VistaReservaAsignaturasActivity extends AppCompatActivity {
    private String jsonString;
    private ArrayList<String> groupList;
    private Map<String, ArrayList<String>> children;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private JSONObject json;
    private Context contexto = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_reservas);

        Intent intencion = getIntent();

        setTitle(intencion.getStringExtra("asignatura"));

        jsonString = intencion.getStringExtra("message");
        try{
            json = new JSONObject(jsonString);
        }catch (Exception e){
            return;
        }

        expandableListView = findViewById(R.id.elvClases);
        createListView();
    }

    //Crea la lista
    private void createListView(){
        assert json != null;

        //Crea el ArrayList de fechas
        createGroupList();

        //Si no hay clases se avisa al usuario y no se crea la lista
        if(groupList.isEmpty()){
            Toast.makeText(contexto, "No existen clases para esta asignatura en una semana", Toast.LENGTH_LONG).show();
            return;
        }
        //Crea MAP con las asociaciones Fecha -> ArrayList<String> textoHijos;
        //Formato de un hijo -> ver getChildren(String fecha)
        createChildren();

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

    //Genera los grupos de la lista
    //ArrayList de fechas
    //Es decir los strings que irán en la lista para ser seleccionados
    private void createGroupList(){
        groupList = new ArrayList<String>();

        for(Iterator<String> it = json.keys(); it.hasNext();)
            groupList.add(it.next());//Añade las fechas

    }

    //Crea el hashmap que alvergará las asociaciones de una fecha con sus clases
    private void createChildren(){
        assert groupList != null;
        children = new HashMap<>();
        for(String fecha : groupList){
            //Clases de una fecha
            ArrayList<String> hijos = getChildren(fecha);
            children.put(fecha, hijos);
        }
    }

    private ArrayList<String> getChildren(String fecha){
        //Constraints
        assert groupList != null;
        assert json != null;
        assert !fecha.equals("");

        ArrayList<String> children = new ArrayList<String>();

        try {
            JSONArray array = json.getJSONArray(fecha);

            for(int i = 0; i < array.length(); ++i){
                //Se consigue los datos de cada clase
                JSONObject jasonsito = ((JSONObject) array.get(i));
                String horaInicio = jasonsito.getString("hora_inicio");
                String horaFin = jasonsito.getString("hora_fin");
                String aula = jasonsito.getString("aula");
                String edificio = jasonsito.getString("edificio");

                //Formato de cómo se va a ver el texto del hijo
                //Esto se vería:
                //<hora_inicio>-<hora_fin>
                //Aula: <aula>
                //Edificio: <edificio>
                String hijo = horaInicio + " - " + horaFin + "\nAula: " + aula + "\nEdificio: " + edificio;

                children.add(hijo);
            }
        }catch (Exception e){

        }
        return children;

    }

    public void volver(View view){ onBackPressed(); }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, MainActivity.class));

        overridePendingTransition(R.transition.volver_transicion_entrada, R.transition.volver_transicion_salida);
    }
}