package pfm.andresgoicoecheaenrique.cryptoplaces;

import android.content.Context;
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

    private final static int[] colors = new int[]{R.color.CATEGORIA1, R.color.CATEGORIA2, R.color.CATEGORIA3, R.color.CATEGORIA4, R.color.CATEGORIA5, R.color.CATEGORIA6, R.color.CATEGORIA7, R.color.CATEGORIA8, R.color.CATEGORIA9, R.color.CATEGORIA10, R.color.CATEGORIA11, R.color.CATEGORIA12, R.color.CATEGORIA13, R.color.CATEGORIA14, R.color.CATEGORIA15, R.color.CATEGORIA16};

    private ArrayList<Venue> venuesAL;
    private GestorBD_Venue gBD;
    private ArrayList<Venue> searchViewVenuesAL = new ArrayList<>();;
    private Context contexto;
    private androidx.fragment.app.FragmentManager FragManager;
    private Boolean isFavs;

    public AdaptadorRecyclerViewVenue(ArrayList<Venue> venues, GestorBD_Venue gBD, Context context, androidx.fragment.app.FragmentManager FragManager, Boolean isFavs) {
        this.venuesAL = venues;
        this.gBD = gBD;
        this.searchViewVenuesAL = new ArrayList<>();//puede fallar
        searchViewVenuesAL.addAll(venues);
        //TEST
        this.FragManager = FragManager;
        this.contexto = context;
        this.isFavs = isFavs;
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
                CommonUtils.jsonParse(url, CommonUtils.leerBBDDSQLite(gBD), contexto, FragManager, isFavs);
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
        notifyDataSetChanged();//NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO TOCAR
    }

    public void filtradoCategoria(Boolean[] options){
        venuesAL.clear();
        if(options[0]){// ALL options
            CommonUtils.mostrarToast("opciontes 0", contexto);
            venuesAL.addAll(searchViewVenuesAL);
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                for(todasCategoriasEnum categorias : todasCategoriasEnum.values()){
                    if(options[categorias.getIndex()+1]){
                        List<Venue> collect = searchViewVenuesAL.stream()
                                .filter(item -> item.getCategory().toLowerCase().equals(categorias.getCategoria()))
                                .collect(Collectors.toList());

                        venuesAL.addAll(collect);
                    }
                }
            }
            else{
                for(todasCategoriasEnum categorias : todasCategoriasEnum.values()){
                    if(options[categorias.getIndex()+1]){
                        for(Venue venue: searchViewVenuesAL){
                            if(venue.getCategory().toLowerCase().equals(categorias.getCategoria())){
                                venuesAL.add(venue);
                            }
                        }

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
            itemIcon.setBackgroundResource(colors[CommonUtils.colorIndex(venue.getCategory())]);
            nombreVenue.setText(venue.getName());
            categVenue.setText(CommonUtils.traducirCategory(venue.getCategory(), contexto));
        }
    }
}
