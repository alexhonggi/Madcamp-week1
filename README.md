# MadCamp_Week1

## 팀원
[윤영훈](https://github.com/wodlxosxos), [이홍기](https://github.com/alexhonggi)

## ABSTRACT

3개의 탭으로 구성된 간단한 앱.  

TAB1: 연락처  
TAB2: 이미지 갤러리  
TAB3: 주변 장소 검색 기능 및 연락처 추가 기능을 탑재한 지도

## TAB 1. Contact

### Features
1. 저장된 연락처 불러오기
2. 연락처 추가하기
3. 연락처 검색하기  
#### 1. 연락처 불러오기  
연락처들을 Array로 저장하여 관리하기 위해 아래와 같은 _`Data`_를 담는 [RecyclerView Adapter](https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=ko)를 사용하였습니다.  
```Java
public class Data {

    private String user_name;
    private String user_number;
```  


초기에는 연락처 정보(name, phone number)를 list형식으로 갖고 있는 **JSON파일**을 만들어 Adapter에 이용했지만,  
 **TAB3**를 구현하면서 **데이터베이스**의 필요성을 느끼게되었습니다.  

[Google Firebase](https://firebase.google.com/)에서 제공되는 Realtime Database를 이용해 연락처를 DB에 저장하였고,   
Firebase API에서 제공되는 [DB Reference](https://firebase.google.com/docs/android/setup?hl=ko)를 이용하여 아래와 같이 DB에 저장된 연락처들을   
Recyclerview Adapter에 담아 화면에 나타내었습니다.  


```Java
private void getData() {
        myRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Data dData = snapshot.getValue(Data.class);
                    adapter.addItem(dData);
                    ...
```  
`myRef.child("users").addValueEventListener()` :  _현재 연동되어 있는 DB에 data가 추가, 변경, 삭제 되었을 때마다 호출되는 함수로 다른 Fragment(Activity)에서의 data 수정시에도 호출되는 함수._



#### 2. 연락처 추가  
Fragment에서 Activity를 호출하여 Activity가 종료될 때 Fragment로 값을 반환해주는 기능을 구현해야 했습니다.  

**Bundle**과 Intent를 이용한 **startActivity()**함수를 사용해보았지만 제대로 작동하지 않았었는데,   
Intent를 이용하여 Activity를 호출하고, 호출된 activity에 존재하는 값들을 Fragment에 반환하여 이용할 수 있게 해주는 [startActivityForResult](https://programming-workspace.tistory.com/47)를 사용하여 해결할 수 있었습니다.  

```Java
imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AdduserActivity.class);
                startActivityForResult(intent, 2);
            }
        });

...
public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            if(data != null){
                String name = data.getStringExtra("name");
                String number = data.getStringExtra("number");
                ...
                myRef.child("users").child(String.valueOf(childNum+1)).setValue(newUser);
```  
반환된 값들을 _onActivityResult_ 함수에서 사용하여 DB Reference를 통해 DB에 새로운 Data를 추가할 수 있게끔 하였습니다.  
(`myRef.child("users").addValueEventListener()`가 호출됨)  

<img width="40%" src="https://user-images.githubusercontent.com/46164736/124593515-b308e800-de99-11eb-800f-e4903ef23539.gif"/>

#### 3. 연락처 검색  


searchButton에 설정된 onClick 함수에서 [AlertDialog](https://developer.android.com/guide/topics/ui/dialogs?hl=ko)를 사용, `String name`을 EditText를 이용해 입력받았습니다.  

adapter(DB)에 존재하는 연락처 중 입력받은 `name`과 동일한 값을 갖는 연락처를 찾고,  
존재하면 해당 연락처의 전화번호를 보여주고,  
 존재하지 않으면 result가 없음을 알려주는 **AlertDialog**를 중첩하여 사용함으로써 검색 결과가 나타나게 구현하였습니다.
```Java
ImageButton searchButton = rootView.findViewById(R.id.searchUserBtn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout searchLinear = (LinearLayout) rootView.inflate(getContext(), R.layout.dialog_user,null);
                final LinearLayout showLinear = (LinearLayout) rootView.inflate(getContext(), R.layout.dialog_show,null);
                new AlertDialog.Builder(getContext()).setView(searchLinear).setPositiveButton("검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ...
                        new AlertDialog.Builder(getContext()).setView(showLinear).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog2, int which) {
                                ...
```

<img width="40%" src="https://user-images.githubusercontent.com/46164736/124593991-3d514c00-de9a-11eb-9535-6726451e34da.gif"/>


## TAB2: Image Gallery

### Features
1. RecyclerView와 GridLayout을 이용한 갤러리 구현
2. 인터넷에서 선택한 이미지 저장

#### 1. RecyclerView와 GridLayout을 이용한 갤러리 구현

갤러리의 기본 기능은 이미지를 **격자로 배열, 스크롤 할 수 있게 전시**하는 것입니다.   

많은 이미지를 스크롤하여 볼 수 있도록 ```RecyclerView``` 를 이용하였고,   
이미지들을 격자 형태로 배열하기 위해 ```GridLayout``` 을 이용하였습니다.  

```Java
@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GalleryAdapter galleryAdapter = new GalleryAdapter(Utils.getData(),this);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(galleryAdapter);
    }
```
#### 2. 인터넷에서 선택한 이미지 저장  
기기에 내장되어있는 앨범의 경우 기기의 카메라로 사진을 촬영해, 내부 저장소에 저장한 것을 디스플레이하는 형식입니다.  
TAB2에서는 기존과 달리, 온라인에서 이미지를 불러올 수 있도록 하였습니다.  

네트워크를 통해 이미지를 불러오는 것은 굉장히 많은 시간이 소요되는 작업이기에,  
[Glide](https://github.com/bumptech/glide) 라이브러리를 이용해 병렬 처리, 이미지 캐싱 기능을 도입하였습니다.

```Java
@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ImageModel image = getArguments().getParcelable(EXTRA_IMAGE);
        String transitionName = getArguments().getString(EXTRA_TRANSITION_NAME);
        final PhotoView imageView = view.findViewById(R.id.detail_image);

        ...

        Glide.with(getContext())
                .load(image.getUrl())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        startPostponedEnterTransition();
                        imageView.setImageDrawable(resource);
                    }
                });

    }
```

선택한 이미지는, 링크의 형태로 ```Utils.java``` 에 저장하였습니다.  
하나 이상의 이미지를, ```ArrayList<ImageModel> arrayList``` 의 형태로 관리합니다.

```Java
public class Utils {

    private static String[] IMGS = {
            "https://gregstoll.com/~gregstoll/baseballteamnames/images/hou_logo.jpg",
            ...    // gif도 저장할 수 있습니다.
            "https://s3.amazonaws.com/www-inside-design/uploads/2019/01/kinetic-typography-1.gif",
            "https://64.media.tumblr.com/8ab37589e31033a3cb4e97ecf04c9d65/tumblr_pbhbrtuQn61u6glkso1_540.gifv"
    };

    public static ArrayList<ImageModel> getData() {
        ArrayList<ImageModel> arrayList = new ArrayList<>();
        for (int i = 0; i < IMGS.length; i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + i);
            imageModel.setUrl(IMGS[i]);
            arrayList.add(imageModel);
        }
        return arrayList;
    }
}

```

<img width="40%" src="https://user-images.githubusercontent.com/46164736/124594526-db451680-de9a-11eb-9871-e6dcd1864cb0.gif"/>
## TAB3: Places

### Features
1. 지도 만들기와 현재 위치 표시하기
2. 장소 검색과 연락처 저장하기
3. 카테고리에 따른 인근 장소 소개

#### 1. 지도 만들기와 현재 위치 표시하기  

[Google Maps Android API](https://developers.google.com/maps/documentation/android-sdk/overview)를 이용하여 구글 지도를 TAB3 프래그먼트에 표시하였습니다.



처음 실행하면 지도의 초기 위치를 [한국과학기술원 본원](https://www.google.com/maps/place/KAIST+%ED%95%9C%EA%B5%AD%EA%B3%BC%ED%95%99%EA%B8%B0%EC%88%A0%EC%9B%90+%EB%8C%80%EB%8D%95%EC%BA%A0%ED%8D%BC%EC%8A%A4/@36.3718117,127.3606118,17z/data=!4m9!1m2!2m1!1z7Lm07J207Iqk7Yq467O47JuQ!3m5!1s0x35654bb616ae884f:0x9fa607e06759a2c9!8m2!3d36.3721427!4d127.36039!15sChLsubTsnbTsiqTtirjrs7jsm5CSARNuYXRpb25hbF91bml2ZXJzaXR5)으로 이동시키고,
위치 정보 사용을 위한 권한을 사용자에게 요청합니다.
```Java
    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION,  
    Manifest.permission.ACCESS_COARSE_LOCATION};  // (1) 위치 관련, (2) GPS
```

[FusedLocationProviderClient](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient)를 이용해, 현재 위치를 마커 상에 표기해줍니다.  



```LocationCallback``` 에서는, ```setCurrentLocation()``` 을 이용하여   
(1) 마커의 위치, (2) 마커의 제목, (3) 마커의 위도 경도 를 설정합니다.

```Java
LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            ...

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);

                // 현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }
        }
    };
```

현재 위치를 GPS를 이용해 획득하므로 ```Geocoder``` 를 이용해 GPS 정보를 위치 주소로 변환하였습니다.
```Java
public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation, latlng.latitude, latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제

...
```
<img width="40%" src="https://user-images.githubusercontent.com/46164736/124595281-c87f1180-de9b-11eb-991b-a05a6bb8a75e.gif"/>


#### 2. 장소 검색과 연락처 저장하기
- `searchMapBtn` 을 클릭 시, [Google Maps Platform](https://developers.google.com/maps/documentation/places/android-sdk/autocomplete)에서 제공된 자동완성기능   
_`AutocompleteActivity`_를 _`startActivityForResult`_로 실행시켰고,  
실행된 검색창에서 선택된 장소의 정보를  `onActivityResult` 로 받아  이용하였습니다.  
`data` 를 이용하여 장소의 위도 & 경도를 전달, 새로운 `Marker`를 등록하였고  
 `data` 가 담겨진 장소의 이름과 번호를 `Marker`의 `title`과 `snippet`에 담아주었습니다.  
  `Marker`를 `map`에 추가해주고, **`onMarkerClick`**에서 이용하기 위해 `Marker`의 `Tag`를 `0`으로 설정하였습니다.
```Java
searchMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
                        Place.Field.PHONE_NUMBER);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("KR")
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
...
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng newLatLng = place.getLatLng();
                Marker tmpMarker = map.addMarker(new MarkerOptions().position(newLatLng).title(place.getName()).snippet(place.getPhoneNumber()));
                currentPosition = newLatLng ;
                map.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                tmpMarker.setTag(0);
```
- **Google Map**에 추가된 `Marker`를 클릭할 시, 이를 이용할 수 있게 하는 _`onMarkerClick`_ 메소드를  이용하였습니다.  
클릭된 `Marker`의 `tag`값을 `1` 증가시켜 클릭되었음을 나타내었고, 클릭된 `Marker`에 담긴 `title`과 `snippet`을 가져와 `title`변수와 `phoneNumber`에 담아주었습니다.  
 이 때,  `phoneNumber`가 `null`이 아닐 때에만, _`AlertDialog`_를 호출하여 `Data(title, phoneNumber)`를 DB Reference에 추가해주었습니다.  
```Java
public boolean onMarkerClick(final Marker marker) {
        Integer clickCount = (Integer) marker.getTag();

        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            String title = marker.getTitle();
            String phoneNumber = marker.getSnippet();
            if (phoneNumber == null){
            }else{
                final LinearLayout addPlaceLinear = (LinearLayout) getView().inflate(getContext(), R.layout.dialog_place,null);
                new AlertDialog.Builder(getContext()).setView(addPlaceLinear).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newNumber = phoneNumber.substring(4);
                        Data newUser = new Data(title, "0"+newNumber);
                        myRef.child("users").child(String.valueOf(childNum+1)).setValue(newUser);
```  
이때 `childNum` 변수는 DB에 존재하는 `"number"` key에 `data` 변화가 생겼을 시 실행되는 `myRef.child("number").addValueEventListener`를 통해 `data` 의  실시간 개수를 담고 있도록 하였습니다.  
```Java
private void getData() {
        myRef.child("number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                childNum = 0;
                if(dataSnapshot.getValue() != null){
                    childNum = Integer.valueOf((String) dataSnapshot.getValue());
                }
            }
        });
    }
```

<img width="40%" src="https://user-images.githubusercontent.com/46164736/124595461-02501800-de9c-11eb-9ed1-e6955448eebc.gif"/>

#### 3. 카테고리에 따른 인근 장소 소개

장소를 종류별로 검색하여 원하는 정보를 얻기 위해, [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview)를
이용하였습니다.

현재 위치 인근의 장소에 대한 정보를 요청하기 위해 Places API Web Service의 REST API를 사용, 그 결과를 JSON으로 파싱하는 방법을 선택할 수 있으나, 간단한 구현을 위해 [Android-Google-Places-API](https://github.com/nomanr/Android-Google-Places-API)를 사용하였습니다.

카테고리에 대한 검색이 직관적일 수 있도록,   
Floating Action Button을 이용해 한 번의 클릭으로 인근의 장소를 불러올 수 있도록 하였습니다.

'**인근**'은 접근성과 니즈를 고려해 도보 도달 가능 범위로 한정하였으나, 서울 외 지역을 고려해 너무 작은 범위는 지양했습니다.  
'**카테고리**'는 필요성을 고려해 검색 빈도가 높은 **카페, 음식점, 대중교통 역**을 선택하였습니다.  


인근 장소 표시는 ```showPlaceInformation``` 메소드를 이용하여 구현하였습니다.  
이 때, 하나 이상의 장소를 묶어 관리하기 위해 ```List<Marker> previous_marker``` 를 정의하여 사용하였습니다.
```Java
public void showPlaceInformation(LatLng location)
{
	map.clear(); //지도 클리어

	if (previous_marker != null)
		previous_marker.clear(); // 지역 정보 마커 클리어

	new NRPlaces.Builder()
			.listener((PlaceListener) this)
			.key("Places API Web Service 키")
			.latlng(location.latitude, location.longitude) // Current Location
			.radius(3000) // 3km 내에서 검색
			.type(PlaceType.RESTAURANT) // (1) Restaurant, (2) Cafe, (3) Bus Station
			.build()
			.execute();
```  

기본적으로는 현재 위치한 장소를 기준으로 한 검색을 지원하나,  
검색의 목적은 *잘 모르는 장소를 파악*하기 위함이므로 검색한 위치를 기준으로 한 인근 장소 검색을 구현할 필요성이 있었습니다.  

​
이는, 기존 검색 기능에서 정의한 변수 ```newLatlng``` 을 ```currentPosition```에 대입함으로써 해결하였습니다.

```Java
if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
           ​...
               ​LatLng newLatLng = place.getLatLng();
               ​Marker tmpMarker = map.addMarker(new MarkerOptions().position(newLatLng).title(place.getName()).snippet(place.getPhoneNumber()));
               ​currentPosition = newLatLng ; // 장소 검색 시 마다 그 위치를 기준 장소로 업데이트
           ...

```

<img width="40%" src="https://user-images.githubusercontent.com/46164736/124595558-23b10400-de9c-11eb-96a9-18ccb7ed875a.gif"/>
