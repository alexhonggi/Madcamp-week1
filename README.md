
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


Use the package manager [pip](https://pip.pypa.io/en/stable/) to install foobar.

```bash
pip install foobar
```

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
        
        (...)

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
            (...)  // gif도 저장할 수 있습니다.
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
이미지 앨범 동영상 (동영상)
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

            (...)

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
        
(...)
```
현재 위치 GPS정보 획득 (동영상)


#### 2. 장소 검색과 연락처 저장하기

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
           ​(...)
               ​LatLng newLatLng = place.getLatLng();
               ​Marker tmpMarker = map.addMarker(new MarkerOptions().position(newLatLng).title(place.getName()).snippet(place.getPhoneNumber()));
               ​currentPosition = newLatLng ; // 장소 검색 시 마다 그 위치를 기준 장소로 업데이트
           (...)
            
```

검색한 장소 인근의 건물 찾기 >
(동영상)

