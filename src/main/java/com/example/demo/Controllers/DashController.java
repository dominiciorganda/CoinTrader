package com.example.demo.Controllers;

import com.example.demo.DTOs.CoinDTO;
import com.example.demo.Mappers.CoinMapper;
import com.example.demo.Services.DashService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
@RequestMapping("/CoinTrader/dash")
@CrossOrigin
public class DashController {

    @Autowired
    private DashService dashService;

    public DashController() throws IOException {
    }

    @GetMapping("/getAll")
    public List<CoinDTO> getAll() {
        return CoinMapper.mapCoinListtoCoinDTOList(dashService.getAll());
    }

    @GetMapping("/getLastMonth")
    public List<CoinDTO> getLastMonth() {
        return CoinMapper.mapCoinListtoCoinDTOList(dashService.getLastMonth());
    }

    @GetMapping("/getMax")
    public CoinDTO getMax() {
        return CoinMapper.mapCointoCoinDTO(dashService.getAllTimeMax());
    }

    @GetMapping("/getLast")
    public CoinDTO getLast() {
        return CoinMapper.mapCointoCoinDTO(dashService.getLast());
    }

    @GetMapping("/getLastX/{number}")
    public List<CoinDTO> getLastX(@PathVariable("number") int num) {
        return CoinMapper.mapCoinListtoCoinDTOList(dashService.getLastX(num));
    }

    @GetMapping(value = "/getActual")
    public CoinDTO getActual() throws IOException {
        return CoinMapper.mapCointoCoinDTO(dashService.getActual());
    }

    @GetMapping("/getAnualMax")
    public CoinDTO getAnualMax() {
        return CoinMapper.mapCointoCoinDTO(dashService.getAnualMax());
    }

    @GetMapping("/getAnualMin")
    public CoinDTO getAnualMin() {
        return CoinMapper.mapCointoCoinDTO(dashService.getAnualMin());
    }

    private static final String CSV_SEPARATOR = ",";

    @Async
    void writeToCSV(List<CoinDTO> coinDTOS) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("CSV\\dashs.csv"), "UTF-8"));
            bw.write("Date" + CSV_SEPARATOR + "Price");
            bw.newLine();
            for (CoinDTO coinDTO : coinDTOS) {
                String oneLine = coinDTO.getDate().replaceAll("\\-", "") +
                        CSV_SEPARATOR +
                        coinDTO.getPrice();
                bw.write(oneLine);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException ignored) {
        }
    }

    @GetMapping("/prediction")
    public ResponseEntity<?> makePrediction() {
        writeToCSV(CoinMapper.mapCoinListtoCoinDTOList(dashService.getLastX(50)));
        LocalDate localDate = LocalDate.now().plusDays(1);
        List<CoinDTO> coinDTOS = new ArrayList<>(getLastX(6));
        try {
            String argument = localDate.toString().replaceAll("\\-", "");

            ProcessBuilder pb = new ProcessBuilder("python", "E:\\licenta\\prediction\\venv\\dashPrediction.py", "" + argument + "-" + argument);
            Process p = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            for (int i = 0; i < 5; i++) {
                String price = in.readLine();
                String date = localDate.plusDays(i).toString();
                coinDTOS.add(new CoinDTO(date, Double.parseDouble(price)));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return ResponseEntity.ok(coinDTOS);
    }
}
