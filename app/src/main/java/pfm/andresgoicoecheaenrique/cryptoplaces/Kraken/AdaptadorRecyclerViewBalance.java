package pfm.andresgoicoecheaenrique.cryptoplaces.Kraken;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pfm.andresgoicoecheaenrique.cryptoplaces.R;

public class AdaptadorRecyclerViewBalance extends RecyclerView.Adapter<AdaptadorRecyclerViewBalance.ViewHolderCrypto> {

    private ArrayList<Criptomoneda> cryptosAL;
    private ArrayList<Criptomoneda> searchViewCryptoAL;

    public AdaptadorRecyclerViewBalance(ArrayList<Criptomoneda> cryptos) {
        this.cryptosAL = cryptos;
        this.searchViewCryptoAL = new ArrayList<>();//puede fallar
        searchViewCryptoAL.addAll(cryptos);
    }


    @NonNull
    @Override
    public AdaptadorRecyclerViewBalance.ViewHolderCrypto onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_crypto_rv, parent, false);
        return new AdaptadorRecyclerViewBalance.ViewHolderCrypto(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorRecyclerViewBalance.ViewHolderCrypto holder, int position) {
        Criptomoneda crypto = cryptosAL.get(position);
        holder.asignarDatos(crypto);
    }

    @Override
    public int getItemCount() {
        return cryptosAL.size();
    }

    public void filtrado(String search){
        if(search.length() == 0){
            cryptosAL.clear();
            cryptosAL.addAll(searchViewCryptoAL);
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                List<Criptomoneda> collect = searchViewCryptoAL.stream()
                        .filter(item -> item.getName().toLowerCase().contains(search))
                        .collect(Collectors.toList());
                cryptosAL.clear();
                cryptosAL.addAll(collect);
            }
            else{
                cryptosAL.clear();
                for(Criptomoneda criptomoneda: searchViewCryptoAL){
                    if(criptomoneda.getName().toLowerCase().contains(search)){
                        cryptosAL.add(criptomoneda);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


    public class ViewHolderCrypto extends RecyclerView.ViewHolder {
        //INICIALIZAR LOS COMPONENTES QUE SE VERAN
        TextView nombreCrypto;
        TextView cantidadCrypto;

        public ViewHolderCrypto(@NonNull View itemView) {
            super(itemView);
            nombreCrypto = (TextView) itemView.findViewById(R.id.nombCryptoTV_id);
            cantidadCrypto = (TextView) itemView.findViewById(R.id.cantidadCryptoTV_id);
        }

        public void asignarDatos(Criptomoneda crypto) {
            nombreCrypto.setText(crypto.getName());
            cantidadCrypto.setText(cantidadCrypto.getText() + crypto.getAmount() + " " + crypto.getName());
        }
    }
}
