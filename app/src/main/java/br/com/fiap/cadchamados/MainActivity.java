package br.com.fiap.cadchamados;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONStringer;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spnTipo;
    private EditText edtCodigo;
    private CheckBox cbxFinalizdo;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Valida o item de menu escolhido
        if (item.getItemId() == R.id.menu_pesquisar){
            //Navegar de tela
            Intent intent = new Intent(this,PesquisarActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spnTipo = (Spinner) findViewById(R.id.spnTipo);
        List<String> lista = this.list();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lista);
        spnTipo.setAdapter(adapter);

        edtCodigo = (EditText) findViewById(R.id.edtCodigo);
        cbxFinalizdo = (CheckBox) findViewById(R.id.cbxFinalizado);
    }

    protected List<String> list(){
        List<String> lista = new ArrayList<>();
        lista.add("Troca de aparelho");
        lista.add("Nova instalação");
        lista.add("Retirada de aparelho");
        return lista;
    }

    public void cadastrar(View view){
        CadastrarTask task = new CadastrarTask();
        String codigo = edtCodigo.getText().toString();
        String tipo = (String) spnTipo.getSelectedItem();
        String finalizdo = (cbxFinalizdo.isSelected())? "true" : "false";
        task.execute(codigo,tipo,finalizdo);
    }

    private class CadastrarTask extends AsyncTask<String, Void, Integer>{

        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Aguarde", "Salvando informações");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            progress.dismiss();
            if(integer == 201 || integer == 200){
                Toast.makeText(MainActivity.this, "Sucesso", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "Falha", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            try{

//                URL url = new URL("https://assistenciaapi.herokuapp.com/rest/chamado/");
                URL url = new URL("http://lg-01.exametoxicologico.com.br/api/email/postback");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");

                JSONStringer json = new JSONStringer();
                json.object();
                json.key("codigoFuncionario").value(params[0]);
                json.key("descricao").value(params[1]);
                json.key("finalizado").value(params[2]);
                json.endObject();


                OutputStreamWriter stream = new OutputStreamWriter(connection.getOutputStream());
                stream.write(json.toString());
                stream.close();

                return  connection.getResponseCode();
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }
}
