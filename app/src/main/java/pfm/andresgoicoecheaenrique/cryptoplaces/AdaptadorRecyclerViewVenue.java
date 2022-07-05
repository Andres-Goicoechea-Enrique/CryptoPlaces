package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.content.Context;
import android.content.Intent;
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

public class AdaptadorRecyclerViewVenue extends RecyclerView.Adapter<AdaptadorRecyclerViewVenue.ViewHolderVenue>{

    private ArrayList<Venue> venuesAL;
    private ArrayList<Venue> searchViewVenuesAL;
    private funcionesLetes methodsUtils;

    public AdaptadorRecyclerViewVenue(ArrayList<Venue> venues, Context context, androidx.fragment.app.FragmentManager FragManager) {
        this.venuesAL = venues;
        this.searchViewVenuesAL = new ArrayList<>();//puede fallar
        searchViewVenuesAL.addAll(venues);
        //TEST
        this.methodsUtils = new funcionesLetes(context, FragManager);
    }

    @NonNull
    @Override
    public ViewHolderVenue onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_venue_rv, parent, false);
        return new AdaptadorRecyclerViewVenue.ViewHolderVenue(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderVenue holder, int position) {
        Venue venue = venuesAL.get(position);
        holder.asignarDatos(venue);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://coinmap.org/api/v1/venues/" + venue.getId();
                methodsUtils.jsonParse(url, 2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return venuesAL.size();
    }

    public void filtrado(String search){
        if(search.length() == 0){
            venuesAL.clear();
            venuesAL.addAll(searchViewVenuesAL);
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                List<Venue> collect = searchViewVenuesAL.stream()
                        .filter(item -> item.getName().toLowerCase().contains(search))
                        .collect(Collectors.toList());
                venuesAL.clear();
                venuesAL.addAll(collect);
            }
            else{
                venuesAL.clear();
                for(Venue venue: searchViewVenuesAL){
                    if(venue.getName().toLowerCase().contains(search)){
                        venuesAL.add(venue);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }



    public class ViewHolderVenue extends RecyclerView.ViewHolder{
        //INICIALIZAR LOS COMPONENTES QUE SE VERAN
        TextView itemIcon;
        TextView nombreVenue;
        TextView categVenue;

        public ViewHolderVenue(@NonNull View itemView) {
            super(itemView);
            itemIcon = (TextView) itemView.findViewById(R.id.itemIconId);
            nombreVenue = (TextView) itemView.findViewById(R.id.nombVenue);
            categVenue = (TextView) itemView.findViewById(R.id.categoriaVenue);
        }

        public void asignarDatos(Venue venue) {
            //Color itemIcon en funcion categoria//ampliar
            itemIcon.setBackgroundResource(R.color.greenAccent);
            nombreVenue.setText(venue.getName());
            categVenue.setText(venue.getCategory());
        }
    }
}
