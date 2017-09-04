package br.com.fiap.cadchamados;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.fiap.cadchamados.Model.Item;

public class PesquisarActivity extends AppCompatActivity {

    private ListView lstIens;
    private EditText edtPesquisa;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.voltar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Valida o item de menu escolhido
        if (item.getItemId() == R.id.menu_voltar){
            //Navegar de tela
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar);

        edtPesquisa = (EditText) findViewById(R.id.edtPesquisa);
        lstIens = (ListView) findViewById(R.id.lstItens);
    }

    public void pesquisar(){
        PesquisaItensTask task = new PesquisaItensTask();
        task.execute(Integer.parseInt(edtPesquisa.getText().toString()));

    }

    private class PesquisaItensTask extends AsyncTask<Integer, Void, String>{

        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(PesquisarActivity.this, "Aguarde","Buscando no servidor");
        }

        @Override
        protected String doInBackground(Integer... params) {
            try{
                URL url = new URL("https://assistenciaapi.herokuapp.com/rest/chamado/funcionario/" + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept","Application/json");

                if (connection.getResponseCode() != 200){
                    Toast.makeText(PesquisarActivity.this, "Nenhum registro encontrado", Toast.LENGTH_SHORT).show();
                    return null;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream())
                );
                StringBuilder json = new StringBuilder();
                String linha;

                while ((linha = reader.readLine()) != null){
                    json.append(linha);
                }
                connection.disconnect();
                return json.toString();

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progress.dismiss(); //fecha o dialogo de progresso
            if (s != null) {
                //Recuperar os valores do JSON
                try {
                    JSONObject json = new JSONObject(s);
                    JSONArray jsonArray = json.getJSONArray("itens");

                    List<Item> lista = new ArrayList<Item>();

                    for (int i=0; i <jsonArray.length(); i++){
                        JSONObject item = (JSONObject) jsonArray.get(i);
                        String codigo = item.getString("codigoFuncionario");
                        String data = item.getString("data");
                        String desc = item.getString("descricao");
                        String finalizado = item.getString("finalizado");

                        Item item1 = new Item(codigo,desc,data,finalizado);
                        lista.add(item1);
                    }

                    //Exibir a lista de itens na tela
                    ListAdapter adapter = new ArrayAdapter(
                            PesquisarActivity.this,android.R.layout.simple_list_item_1,
                            lista);
                    lstIens.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PesquisarActivity.this, "Erro", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
