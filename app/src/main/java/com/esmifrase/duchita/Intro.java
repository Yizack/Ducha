package com.esmifrase.duchita;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class Intro extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("Uso del Agua");
        sliderPage1.setDescription("¿Sabías que la mayor del agua que se utiliza en el hogar es en el cuarto de baño?");
        sliderPage1.setImageDrawable(R.drawable.intro1);
        sliderPage1.setBgColor(getResources().getColor(R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Gasto de agua en la ducha");
        sliderPage2.setDescription("Para menor impacto medioambiental cronometrándonos, habría que reducirla a 5 minutos = 100 litros de consumo.");
        sliderPage2.setImageDrawable(R.drawable.intro2);
        sliderPage2.setBgColor(getResources().getColor(R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("¡Comienza a ahorrar!");
        sliderPage3.setDescription("Lee nuestra guía en el menú principal y descubre funciones como: Cronómetro, Modo Shampoo/Acondicionador y Reproductor de música.");
        sliderPage3.setImageDrawable(R.drawable.intro3);
        sliderPage3.setBgColor(getResources().getColor(R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

    }

    @Override
    public void onSkipPressed(android.support.v4.app.Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(android.support.v4.app.Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
