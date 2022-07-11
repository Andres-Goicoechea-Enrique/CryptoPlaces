package pfm.andresgoicoecheaenrique.cryptoplaces.Kraken;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pfm.andresgoicoecheaenrique.cryptoplaces.R;
import pfm.andresgoicoecheaenrique.cryptoplaces.Venue;

public class AdaptadorRecyclerViewAPIs extends RecyclerView.Adapter<AdaptadorRecyclerViewAPIs.ViewHolderAPI>{

    private ArrayList<ExchangeAPI> apisAL;
    private ArrayList<ExchangeAPI> searchViewAPIsAL;
    private TextView cantidadAPIs;
    private Context contexto;

    public AdaptadorRecyclerViewAPIs(ArrayList<ExchangeAPI> apis, TextView cantidad, Context contexto) {
        this.apisAL = apis;
        this.searchViewAPIsAL = new ArrayList<>();//puede fallar
        searchViewAPIsAL.addAll(apis);
        this.cantidadAPIs = cantidad;
        this.contexto = contexto;
    }

    @NonNull
    @Override
    public AdaptadorRecyclerViewAPIs.ViewHolderAPI onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_api_rv, parent, false);
        return new AdaptadorRecyclerViewAPIs.ViewHolderAPI(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorRecyclerViewAPIs.ViewHolderAPI holder, int position) {
        ExchangeAPI api = apisAL.get(position);
        holder.asignarDatos(api);

        holder.deleteAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apisAL.remove(api);
                cantidadAPIs.setText(contexto.getResources().getString(R.string.cantidad1_tv) + apisAL.size() + contexto.getResources().getString(R.string.cantidad3_apis_tv));
                // BASE DE DATOS LOCAL REALIZAR EL BORRADO SQLLITE
                //
                //notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), MostrarBalanceActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apisAL.size();
    }

public void filtrado(String search){
        if(search.length() == 0){
            apisAL.clear();
            apisAL.addAll(searchViewAPIsAL);
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                List<ExchangeAPI> collect = searchViewAPIsAL.stream()
                        .filter(item -> item.getName().toLowerCase().contains(search))
                        .collect(Collectors.toList());
                apisAL.clear();
                apisAL.addAll(collect);
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolderAPI extends RecyclerView.ViewHolder{
        //INICIALIZAR LOS COMPONENTES QUE SE VERAN

        TextView nombreAPI;
        ImageButton deleteAPI;

        public ViewHolderAPI(@NonNull View itemView) {
            super(itemView);
            nombreAPI = (TextView) itemView.findViewById(R.id.nombAPI_TV_id);
            deleteAPI = (ImageButton) itemView.findViewById(R.id.deleteAPI_IB_id);
        }

        public void asignarDatos(ExchangeAPI exchangeAPI) {
            nombreAPI.setText(exchangeAPI.getName());
        }
    }

}
