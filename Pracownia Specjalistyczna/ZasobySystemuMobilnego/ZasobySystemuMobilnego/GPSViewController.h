//
//  GPSViewController.h
//  ZasobySystemuMobilnego
//
//  Created by Mateusz_ on 13/12/2023.
//
#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GPSViewController : UIViewController <CLLocationManagerDelegate>

@property (weak, nonatomic) IBOutlet UILabel *latitudeValueLabel;
@property (weak, nonatomic) IBOutlet UILabel *longtitudeValueLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressValueLabel;
@property (weak, nonatomic) IBOutlet UIButton *currentLocationButton;

- (IBAction)getCurrentLocation:(id)sender;

@end

NS_ASSUME_NONNULL_END
